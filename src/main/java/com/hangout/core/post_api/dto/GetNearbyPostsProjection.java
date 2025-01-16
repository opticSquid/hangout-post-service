package com.hangout.core.post_api.dto;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.locationtech.jts.geom.Point;

public interface GetNearbyPostsProjection {
    UUID getPostId();

    BigInteger getOwnerId();

    String getFilename();

    String getContentType();

    String getPostDescription();

    Integer getHearts();

    Integer getComments();

    Integer getInteractions();

    ZonedDateTime getCreatedAt();

    Point getLocation();

    Double getDistance();
}
