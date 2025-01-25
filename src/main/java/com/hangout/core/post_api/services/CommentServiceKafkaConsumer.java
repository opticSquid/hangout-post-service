package com.hangout.core.post_api.services;

import java.util.Optional;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.hangout.core.post_api.dto.event.CommentEvent;
import com.hangout.core.post_api.entities.Comment;
import com.hangout.core.post_api.entities.HierarchyKeeper;
import com.hangout.core.post_api.entities.Post;
import com.hangout.core.post_api.repositories.CommentRepo;
import com.hangout.core.post_api.repositories.HierarchyKeeperRepo;
import com.hangout.core.post_api.repositories.PostRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceKafkaConsumer {
    private final CommentRepo commentRepo;
    private final HierarchyKeeperRepo hkRepo;
    private final PostRepo postRepo;
    private final PostService postService;

    @KafkaListener(topics = "${hangout.kafka.comment.topic}", groupId = "${spring.application.name}")
    public void createComment(CommentEvent comment) {
        if (comment.parentCommentId().isEmpty()) {
            createTopLevelComment(comment);
        } else {
            createSubComments(comment);
        }
    }

    @Transactional
    private void createTopLevelComment(CommentEvent comment) {
        Post post = postService.getParticularPost(comment.postId());
        if (post != null) {
            Comment topLevelComment = new Comment(post, comment.userId(), comment.comment(), true);
            postRepo.increaseCommentCount(post.getPostId());
            commentRepo.save(topLevelComment);
        }
    }

    @Transactional
    public void createSubComments(CommentEvent reply) {
        Optional<Comment> maybeParentComment = commentRepo.findById(reply.parentCommentId().get());
        if (maybeParentComment.isPresent()) {
            Comment parentComment = maybeParentComment.get();
            Post post = postService.getParticularPost(parentComment.getPost().getPostId());
            Comment childComment = new Comment(post, reply.userId(), reply.comment(), false);
            postRepo.increaseCommentCount(post.getPostId());
            childComment = commentRepo.save(childComment);
            HierarchyKeeper hierarchy = new HierarchyKeeper();
            hierarchy.setParentComment(parentComment);
            hierarchy.setChildCommnet(childComment);
            hkRepo.save(hierarchy);
        }
    }
}
