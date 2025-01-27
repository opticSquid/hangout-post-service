package com.hangout.core.post_api.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hangout.core.post_api.entities.HierarchyKeeper;
import com.hangout.core.post_api.projections.FetchCommentProjection;

public interface HierarchyKeeperRepo extends JpaRepository<HierarchyKeeper, Integer> {
    @Query(value = "SELECT c.comment_id, c.created_at, c.text, c.user_id, c.replies FROM comment AS c JOIN hierarchy_keeper k ON c.comment_id = k.child_comment_id WHERE k.parent_comment_id = :commentId", nativeQuery = true)
    List<FetchCommentProjection> findAllChildComments(@Param("commentId") UUID commentId);
}
