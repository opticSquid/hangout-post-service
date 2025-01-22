package com.hangout.core.post_api.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hangout.core.post_api.dto.CommentDTO;
import com.hangout.core.post_api.dto.NewCommentRequest;
import com.hangout.core.post_api.dto.Reply;
import com.hangout.core.post_api.entities.Comment;
import com.hangout.core.post_api.entities.HierarchyKeeper;
import com.hangout.core.post_api.entities.Post;
import com.hangout.core.post_api.projections.FetchCommentProjection;
import com.hangout.core.post_api.repositories.CommentRepo;
import com.hangout.core.post_api.repositories.HierarchyKeeperRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepo commentRepo;
    private final HierarchyKeeperRepo hkRepo;
    private final PostService postService;

    private Comment saveComment(Comment comment) {
        return commentRepo.save(comment);
    }

    @Transactional
    public String createTopLevelComment(NewCommentRequest comment) {
        Post post = postService.getParticularPost(comment.postId());
        if (post != null) {
            Comment topLevelComment = new Comment();
            topLevelComment.setTopLevel(true);
            topLevelComment.setPost(post);
            topLevelComment.setText(comment.comment());
            postService.increaseCommentCount(post.getPostId());
            topLevelComment = saveComment(topLevelComment);
            return topLevelComment.getCommentId().toString();
        } else {
            return null;
        }
    }

    @Transactional
    public String createSubComments(Reply reply) {
        Optional<Comment> maybeParentComment = commentRepo.findById(reply.parentCommentId());
        if (maybeParentComment.isPresent()) {
            Comment parentComment = maybeParentComment.get();
            Comment childComment = new Comment();
            childComment.setTopLevel(false);
            childComment.setText(reply.comment());
            Post post = postService.getParticularPost(parentComment.getPost().getPostId());
            childComment.setPost(post);
            postService.increaseCommentCount(post.getPostId());
            childComment = saveComment(childComment);
            HierarchyKeeper hierarchy = new HierarchyKeeper();
            hierarchy.setParentComment(parentComment);
            hierarchy.setChildCommnet(childComment);
            hkRepo.save(hierarchy);
            return childComment.getCommentId().toString();
        } else {
            return null;
        }
    }

    public List<CommentDTO> fetchTopLevelCommentsForAPost(String postId) {
        UUID postIdAsUUID = UUID.fromString(postId);
        List<FetchCommentProjection> model = commentRepo.fetchTopLevelComments(postIdAsUUID);
        return model.stream()
                .map(comment -> new CommentDTO(comment.getCommentid(),
                        Timestamp.from(comment.getCreatedat()),
                        comment.getText(), comment.getUserid()))
                .toList();
    }

    public List<CommentDTO> fetchAllChildCommentsForAComment(String parentCommentId) {
        UUID parentCommentIdUUID = UUID.fromString(parentCommentId);
        List<FetchCommentProjection> model = hkRepo.findAllChildComments(parentCommentIdUUID);
        return model.stream()
                .map(comment -> new CommentDTO(comment.getCommentid(),
                        Timestamp.from(comment.getCreatedat()),
                        comment.getText(), comment.getUserid()))
                .toList();
    }
}
