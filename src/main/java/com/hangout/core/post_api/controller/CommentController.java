package com.hangout.core.post_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hangout.core.post_api.dto.CommentDTO;
import com.hangout.core.post_api.dto.DefaultResponse;
import com.hangout.core.post_api.dto.NewCommentRequest;
import com.hangout.core.post_api.dto.Reply;
import com.hangout.core.post_api.services.CommentService;
import com.hangout.core.post_api.services.CommentServiceKafkaProducer;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/comment")
public class CommentController {
    private final CommentServiceKafkaProducer commentProducerService;
    private final CommentService commentService;

    @Observed(name = "create-top-level-comment", contextualName = "controller")
    @PostMapping
    public DefaultResponse createTopLevelComment(@RequestHeader(name = "Authorization") String authToken,
            @RequestBody NewCommentRequest comment) {
        return commentProducerService.createTopLevelComment(authToken, comment);
    }

    @Observed(name = "reply-to-comment", contextualName = "controller")
    @PostMapping("/reply")
    public DefaultResponse createSubComment(@RequestHeader(name = "Authorization") String authToken,
            @RequestBody Reply reply) {
        return commentProducerService.createSubComments(authToken, reply);
    }

    @Observed(name = "get-all-top-level-comments", contextualName = "controller")
    @GetMapping("/all/{postId}")
    public List<CommentDTO> getAllTopLevelComments(@PathVariable UUID postId) {
        return commentService.fetchTopLevelCommentsForAPost(postId);
    }

    @Observed(name = "get-replies-to-a-comment", contextualName = "controller")
    @GetMapping("/replies/{parentCommentId}")
    public List<CommentDTO> getAllChildCommentsOfAParentComment(@PathVariable UUID parentCommentId) {
        return commentService.fetchAllChildCommentsForAComment(parentCommentId);
    }
}
