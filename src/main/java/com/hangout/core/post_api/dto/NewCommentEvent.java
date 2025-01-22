package com.hangout.core.post_api.dto;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

public record NewCommentEvent(UUID postId, BigInteger userId, Optional<UUID> parentCommentId, String comment) {

}
