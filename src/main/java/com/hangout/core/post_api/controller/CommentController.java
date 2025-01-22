package com.hangout.core.post_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hangout.core.post_api.dto.CommentDTO;
import com.hangout.core.post_api.dto.NewCommentRequest;
import com.hangout.core.post_api.dto.Reply;
import com.hangout.core.post_api.services.CommentService;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @Observed(name = "create-top-level-comment", contextualName = "controller")
    @PostMapping
    public String createTopLevelComment(@RequestBody NewCommentRequest comment) {
        return commentService.createTopLevelComment(comment);
    }

    @Observed(name = "reply-to-comment", contextualName = "controller")
    @PostMapping("/reply")
    public String createSubComment(@RequestBody Reply reply) {
        return commentService.createSubComments(reply);
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
