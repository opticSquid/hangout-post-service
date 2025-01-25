package com.hangout.core.post_api.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Media {
    @Id
    @Column(length = 513)
    private String filename;
    private String contentType;
    @JsonManagedReference
    @OneToMany(mappedBy = "media")
    private List<Post> posts;
    @JsonIgnore
    @Enumerated(value = EnumType.STRING)
    private ProcessStatus processStatus;

    public Media(String hashedFilename, String contentType) {
        this.filename = hashedFilename;
        this.contentType = contentType;
        this.posts = new ArrayList<>();
        this.processStatus = ProcessStatus.IN_QUEUE;
    }

    public void addPost(Post post) {
        if (this.posts.isEmpty()) {
            this.processStatus = ProcessStatus.IN_QUEUE;
            this.posts = new ArrayList<>();
        }
        this.posts.add(post);
    }
}