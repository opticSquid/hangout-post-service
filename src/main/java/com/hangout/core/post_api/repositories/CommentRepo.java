package com.hangout.core.post_api.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hangout.core.post_api.entities.Comment;
import com.hangout.core.post_api.projections.FetchCommentProjection;

import jakarta.transaction.Transactional;

public interface CommentRepo extends JpaRepository<Comment, UUID> {
    @Query(value = "SELECT comment_id, created_at, text, user_id, replies FROM comment where post_id = :postid AND top_level = true;", nativeQuery = true)
    List<FetchCommentProjection> fetchTopLevelComments(@Param("postid") UUID postid);

    @Query(value = "SELECT comment_id, created_at, text, user_id, replies FROM comment where comment_id = :commentId;", nativeQuery = true)
    Optional<FetchCommentProjection> fetchCommentById(@Param("commentId") UUID commentId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE comment SET replies = replies+1 where comment_id = :commentId", nativeQuery = true)
    void increaseReplyCount(@Param("commentId") UUID commentId);
}
