package com.hangout.core.post_api.dto;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;

public interface GetParticularPostProjection {
    UUID getPostId();

    BigInteger getOwnerId();

    String getFilename();

    String getContentType();

    String getPostDescription();

    Integer getHearts();

    Integer getComments();

    Integer getInteractions();

    Instant getCreatedAt();

    String getState();

    String getCity();

    Point<G2D> getLocation();
}
