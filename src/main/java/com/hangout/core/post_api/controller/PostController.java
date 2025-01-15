package com.hangout.core.post_api.controller;

import java.util.List;
import java.util.Optional;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hangout.core.post_api.dto.GetPostsDTO;
import com.hangout.core.post_api.dto.PostCreationResponse;
import com.hangout.core.post_api.entities.Post;
import com.hangout.core.post_api.services.PostService;

import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/post")
public class PostController {
    private final PostService postService;

    @Observed(name = "create-post", contextualName = "controller", lowCardinalityKeyValues = { "postType",
            "MEDIA+TEXT" })
    @PostMapping(path = "/full", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostCreationResponse> createPostWithMediasAndText(
            @RequestHeader(name = "Authorization") String authToken,
            @RequestPart(value = "file") MultipartFile file,
            @RequestPart(value = "postDescription") String postDescription,
            @RequestPart(value = "lat") Double lat,
            @RequestPart(value = "lon") Double lon) throws FileUploadException {
        return new ResponseEntity<>(this.postService.create(authToken, file, Optional.of(postDescription), lat, lon),
                HttpStatus.CREATED);
    }

    @Observed(name = "create-post", contextualName = "controller", lowCardinalityKeyValues = { "postType", "MEDIA" })
    @PostMapping(path = "/short", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostCreationResponse> createPostWithMedias(
            @RequestHeader(name = "Authorization") String authToken,
            @RequestPart(value = "file") MultipartFile file,
            @RequestPart(value = "lat") Double lat,
            @RequestPart(value = "lon") Double lon) throws FileUploadException {
        return new ResponseEntity<>(this.postService.create(authToken, file, Optional.empty(), lat, lon),
                HttpStatus.CREATED);
    }

    @Observed(name = "get-all-posts", contextualName = "controller")
    @GetMapping("/all")
    public List<Post> getAllPosts(@RequestBody GetPostsDTO getPostParams) {
        return this.postService.findAll(getPostParams);
    }

    @Observed(name = "get-particular-post", contextualName = "controller")
    @GetMapping("/{postId}")
    public Post getAParticularPost(@PathVariable String postId) {
        return this.postService.getParticularPost(postId);
    }
}
