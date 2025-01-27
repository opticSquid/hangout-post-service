package com.hangout.core.post_api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class HierarchyKeeper {
    @Id
    @GeneratedValue
    private Integer keeperId;
    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    Comment parentComment;
    @ManyToOne
    @JoinColumn(name = "child_comment_id")
    Comment childComment;

    public HierarchyKeeper(Comment parentComment, Comment childComment) {
        this.parentComment = parentComment;
        this.childComment = childComment;
    }

}
