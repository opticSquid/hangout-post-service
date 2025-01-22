package com.hangout.core.post_api.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hangout.core.post_api.dto.CommentDTO;
import com.hangout.core.post_api.projections.FetchCommentProjection;
import com.hangout.core.post_api.repositories.CommentRepo;
import com.hangout.core.post_api.repositories.HierarchyKeeperRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepo commentRepo;
    private final HierarchyKeeperRepo hkRepo;

    public List<CommentDTO> fetchTopLevelCommentsForAPost(UUID postId) {
        UUID postIdAsUUID = postId;
        List<FetchCommentProjection> model = commentRepo.fetchTopLevelComments(postIdAsUUID);
        return model.stream()
                .map(comment -> new CommentDTO(comment.getCommentid(),
                        Timestamp.from(comment.getCreatedat()),
                        comment.getText(), comment.getUserid()))
                .toList();
    }

    public List<CommentDTO> fetchAllChildCommentsForAComment(UUID parentCommentId) {
        UUID parentCommentIdUUID = parentCommentId;
        List<FetchCommentProjection> model = hkRepo.findAllChildComments(parentCommentIdUUID);
        return model.stream()
                .map(comment -> new CommentDTO(comment.getCommentid(),
                        Timestamp.from(comment.getCreatedat()),
                        comment.getText(), comment.getUserid()))
                .toList();
    }

}
