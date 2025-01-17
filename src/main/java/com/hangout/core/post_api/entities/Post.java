package com.hangout.core.post_api.entities;

import java.math.BigInteger;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.Column;
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
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID postId;
    private BigInteger ownerId;
    @ManyToOne
    @JoinColumn(name = "filename")
    private Media media;
    @Column(length = 500)
    private String postDescription;
    @ManyToOne
    @JoinColumn(name = "address")
    private Address address;
    @Column(columnDefinition = "geography(Point,4326)")
    private Point location;
    @JsonProperty(access = Access.READ_ONLY)
    private final Integer hearts = 0;
    @JsonProperty(access = Access.READ_ONLY)
    private final Integer comments = 0;
    @JsonProperty(access = Access.READ_ONLY)
    private final Integer interactions = 0;
    @JsonProperty(access = Access.READ_ONLY)
    private final ZonedDateTime createdAt = ZonedDateTime.now(ZoneOffset.UTC);
    @JsonIgnore
    private final Boolean publish = true;

    public Post(BigInteger ownerId, Media media, String postDescription, Address address, Point location) {
        this.ownerId = ownerId;
        this.media = media;
        this.postDescription = postDescription;
        this.address = address;
        this.location = location;
    }

    public Post(BigInteger ownerId, Media media, Address address, Point location) {
        this.ownerId = ownerId;
        this.media = media;
        this.address = address;
        this.location = location;
    }
}
