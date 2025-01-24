package com.hangout.core.post_api.entities;

import java.math.BigInteger;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Heart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private BigInteger heartId;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    private BigInteger userId;

    public Heart(Post post, BigInteger userId) {
        this.post = post;
        this.userId = userId;
    }

}
