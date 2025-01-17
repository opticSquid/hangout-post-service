package com.hangout.core.post_api.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer addressId;
    private String state;
    private String city;
    @OneToMany(mappedBy = "address")
    private List<Post> posts;

    public Address(String state, String city) {
        this.state = state;
        this.city = city;
    }

    public void addPost(Post post) {
        if (this.posts.isEmpty()) {
            this.posts = new ArrayList<>();
        }
        this.posts.add(post);
    }
}
