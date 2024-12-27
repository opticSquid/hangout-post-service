package com.hangout.core.post_api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Media {
    @Id
    @Column(length = 513)
    private String hashedFilename;
    private String contentType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public Media(String hashedFilename, String contentType, Post post) {
        this.hashedFilename = hashedFilename;
        this.contentType = contentType;
        this.post = post;
    }

}
