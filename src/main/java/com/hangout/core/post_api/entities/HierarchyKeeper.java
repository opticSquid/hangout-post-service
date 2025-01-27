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
    @Column(name = "keeperid")
    private Integer keeperId;
    @ManyToOne
    @JoinColumn(name = "parentcommentid")
    Comment parentComment;
    @ManyToOne
    @JoinColumn(name = "childcommentid")
    Comment childComment;

    public HierarchyKeeper(Comment parentComment, Comment childComment) {
        this.parentComment = parentComment;
        this.childComment = childComment;
    }

}
