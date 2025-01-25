package com.hangout.core.post_api.entities;

import java.math.BigInteger;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "commentid")
    private UUID commentId;
    @ManyToOne
    @JoinColumn(name = "postid", referencedColumnName = "postId")
    private Post post;
    @JsonProperty(access = Access.READ_ONLY)
    @Column(name = "userid")
    private BigInteger userId;
    @Column(length = 500)
    private String text;
    @Column(name = "toplevel")
    private Boolean topLevel;
    @JsonProperty(access = Access.READ_ONLY)
    @Column(name = "createdat")
    private final ZonedDateTime createdAt = ZonedDateTime.now(ZoneOffset.UTC);
    @JsonProperty(access = Access.READ_ONLY)
    private final Integer replies = 0;

    public Comment(Post post, BigInteger userId, String text, Boolean topLevel) {
        this.post = post;
        this.userId = userId;
        this.text = text;
        this.topLevel = topLevel;
    }

}