package com.hangout.core.post_api.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hangout.core.post_api.entities.Comment;
import com.hangout.core.post_api.projections.FetchCommentProjection;

public interface CommentRepo extends JpaRepository<Comment, UUID> {
    @Query(value = "SELECT commentid, createdat, text, userid FROM comment where postid = :postid AND toplevel = true;", nativeQuery = true)
    List<FetchCommentProjection> fetchTopLevelComments(@Param("postid") UUID postid);

    @Query(value = "SELECT commentid, createdat, text, userid FROM comment where commentid = :commentId;", nativeQuery = true)
    Optional<FetchCommentProjection> fetchCommentById(@Param("commentId") UUID commentId);
}
