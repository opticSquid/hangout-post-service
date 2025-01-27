package com.hangout.core.post_api.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.hangout.core.post_api.dto.CommentCreationResponse;
import com.hangout.core.post_api.dto.CommentDTO;
import com.hangout.core.post_api.dto.NewCommentRequest;
import com.hangout.core.post_api.dto.Reply;
import com.hangout.core.post_api.dto.Session;
import com.hangout.core.post_api.dto.UserValidationRequest;
import com.hangout.core.post_api.entities.Comment;
import com.hangout.core.post_api.entities.HierarchyKeeper;
import com.hangout.core.post_api.entities.Post;
import com.hangout.core.post_api.exceptions.NoDataFound;
import com.hangout.core.post_api.exceptions.UnauthorizedAccessException;
import com.hangout.core.post_api.projections.FetchCommentProjection;
import com.hangout.core.post_api.repositories.CommentRepo;
import com.hangout.core.post_api.repositories.HierarchyKeeperRepo;
import com.hangout.core.post_api.repositories.PostRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final RestClient restClient;
    private final PostRepo postRepo;
    private final CommentRepo commentRepo;
    private final HierarchyKeeperRepo hkRepo;
    @Value("${hangout.auth-service.url}")
    private String authServiceURL;

    public CommentCreationResponse createTopLevelComment(String authToken, NewCommentRequest comment) {
        Session session = authorizeUser(authToken);
        if (session.userId() != null) {
            Optional<Post> post = postRepo.findById(comment.postId());
            if (post.isPresent()) {
                Comment topLevelComment = new Comment(post.get(), session.userId(), comment.comment(), true);
                postRepo.increaseCommentCount(post.get().getPostId());
                topLevelComment = commentRepo.save(topLevelComment);
                return new CommentCreationResponse("comment posted", topLevelComment.getCommentId());
            } else {
                throw new NoDataFound("no post found");
            }
        } else {
            throw new UnauthorizedAccessException("user unauthorized. Can not create comment");
        }
    }

    public CommentCreationResponse createSubComments(String authToken, Reply reply) {
        Session session = authorizeUser(authToken);
        if (session.userId() != null) {
            Optional<Comment> maybeParentComment = commentRepo.findById(reply.parentCommentId());
            if (maybeParentComment.isPresent()) {
                Comment parentComment = maybeParentComment.get();
                Optional<Post> post = postRepo.findById(parentComment.getPost().getPostId());
                Comment childComment = new Comment(post.get(), session.userId(), reply.comment(), false);
                postRepo.increaseCommentCount(post.get().getPostId());
                childComment = commentRepo.save(childComment);
                HierarchyKeeper hierarchy = new HierarchyKeeper(parentComment, childComment);
                hkRepo.save(hierarchy);
                return new CommentCreationResponse("comment posted", childComment.getCommentId());
            } else {
                throw new NoDataFound("no parent comment found");
            }
        } else {
            throw new UnauthorizedAccessException("user unauthorized. Can not create comment");
        }
    }

    public List<CommentDTO> fetchTopLevelCommentsForAPost(UUID postId) {
        UUID postIdAsUUID = postId;
        List<FetchCommentProjection> model = commentRepo.fetchTopLevelComments(postIdAsUUID);
        return model.stream()
                .map(comment -> new CommentDTO(comment.getCommentid(),
                        comment.getCreatedat(),
                        comment.getText(), comment.getUserid()))
                .toList();
    }

    public CommentDTO fetchParticularComment(UUID commentId) {
        Optional<FetchCommentProjection> comment = commentRepo.fetchCommentById(commentId);
        if (comment.isPresent()) {
            return new CommentDTO(comment.get().getCommentid(),
                    comment.get().getCreatedat(), comment.get().getText(),
                    comment.get().getUserid());
        } else {
            throw new NoDataFound("No Comment was found with the given id");
        }

    }

    public List<CommentDTO> fetchAllChildCommentsForAComment(UUID parentCommentId) {
        UUID parentCommentIdUUID = parentCommentId;
        List<FetchCommentProjection> model = hkRepo.findAllChildComments(parentCommentIdUUID);
        return model.stream()
                .map(comment -> new CommentDTO(comment.getCommentid(),
                        comment.getCreatedat(),
                        comment.getText(), comment.getUserid()))
                .toList();
    }

    private Session authorizeUser(String authHeader) {
        ResponseEntity<Session> response = restClient
                .post()
                .uri(authServiceURL + "/auth-api/v1/internal/validate")
                .body(new UserValidationRequest(authHeader))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(Session.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new UnauthorizedAccessException(
                    "User is not valid or user does not have permission to perform current action");
        }
    }
}
