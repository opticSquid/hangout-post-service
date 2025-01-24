package com.hangout.core.post_api.dto.event;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

public record CommentEvent(UUID postId, BigInteger userId, Optional<UUID> parentCommentId, String comment) {

}
