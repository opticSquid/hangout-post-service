package com.hangout.core.post_api.services;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import com.hangout.core.post_api.dto.FileUploadEvent;
import com.hangout.core.post_api.dto.PostCreationResponse;
import com.hangout.core.post_api.dto.UploadedMediaDetails;
import com.hangout.core.post_api.dto.User;
import com.hangout.core.post_api.dto.UserValidationRequest;
import com.hangout.core.post_api.entities.Media;
import com.hangout.core.post_api.entities.Post;
import com.hangout.core.post_api.exceptions.FileUploadFailed;
import com.hangout.core.post_api.exceptions.UnauthorizedAccessException;
import com.hangout.core.post_api.exceptions.UnsupportedMediaType;
import com.hangout.core.post_api.repositories.PostRepo;

import io.micrometer.observation.annotation.Observed;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepo postRepo;
    private final RestClient restClient;
    private final HashService hashService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${hangout.auth-service.url}")
    private String authServiceURL;
    @Value("${hangout.media.upload-path}")
    private String storageServicePath;
    @Value("${hangout.kafka.topic}")
    private String topic;

    @Observed(name = "create-post", contextualName = "create post service")
    @Transactional
    public PostCreationResponse create(String authToken, List<MultipartFile> files,
            Optional<String> postDescription) {
        // authorize the user. this method may throw exception but this exception is
        // handled in global exception handler
        User user = authorizeUser(authToken);
        // create the post object
        Post post = new Post(user.userId(), postDescription.isPresent() ? postDescription.get() : "");
        // upload files to shared path and get their file names
        List<UploadedMediaDetails> uploadedMediaFiles = uploadMedias(user, files);
        List<Media> postMedias = uploadedMediaFiles.parallelStream()
                .map(file -> {
                    // produce events in kafka for successful file uploads
                    // this events will be consumed by storage service
                    kafkaTemplate.send(topic, file.contentType(),
                            new FileUploadEvent(file.derivedFilename(), user.userId()));
                    // create a list of media files to be added to db
                    return new Media(file.derivedFilename(), file.contentType(), post);
                })
                .toList();
        // setting the media in post object
        post.setPostMedias(postMedias);
        // save the post in db
        Post newPost = postRepo.save(post);
        // return post id
        return new PostCreationResponse(uploadedMediaFiles.size(), newPost.getPostId().toString());
    }

    @Observed(name = "get-all-posts", contextualName = "service")
    public List<Post> findAll() {
        return postRepo.findAll();
    }

    public Post getParticularPost(String postId) {
        Optional<Post> maybepost = postRepo.findById(UUID.fromString(postId));
        if (maybepost.isPresent()) {
            postRepo.increaseInteractionCount(UUID.fromString(postId));
            return maybepost.get();
        } else {
            return null;
        }
    }

    public void increaseCommentCount(UUID postId) {
        postRepo.increaseCommentCount(postId);
    }

    public void increaseHeartCount(UUID postId) {
        postRepo.increaseHeartCount(postId);
    }

    // ? kept public for observalibility
    @Observed(name = "create-post", contextualName = "upload media service")
    public List<UploadedMediaDetails> uploadMedias(User user, List<MultipartFile> files) {
        List<UploadedMediaDetails> details = new LinkedList<>();
        // lets upload all the files given by the user in the post to storage service.
        files.parallelStream().forEach(file -> {
            // check file content
            if (file.getContentType().startsWith("image/") || file.getContentType().startsWith("video/")) {
                // create a hash for the file names of each file
                String hashedFilename = hashService.hash(user);
                // get the originalFilename split in the '.' position and get the extension name
                int lastDotIndex = file.getOriginalFilename().lastIndexOf('.');
                String originalfileExtension = file.getOriginalFilename().substring(lastDotIndex + 1);
                hashedFilename += "." + originalfileExtension;
                // save the multipart file to the given destination on disk
                try {
                    file.transferTo(new File(storageServicePath + "/" + hashedFilename));
                } catch (IllegalStateException | IOException e) {
                    throw new FileUploadFailed("Failed to upload file: " +
                            file.getOriginalFilename());
                }
                // add the file details to list
                details.add(new UploadedMediaDetails(hashedFilename, file.getContentType()));
            } else {
                throw new UnsupportedMediaType(file.getOriginalFilename()
                        + " is not supported. Please upload a supported format of multimedia file");
            }

        });
        return details;
    }

    private User authorizeUser(String authHeader) throws UnauthorizedAccessException {
        ResponseEntity<User> response = restClient
                .post()
                .uri(authServiceURL + "/auth/v1/internal/validate")
                .body(new UserValidationRequest(authHeader))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(User.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new UnauthorizedAccessException(
                    "User is not valid or user does not have permission to perform current action");
        }
    }

}
