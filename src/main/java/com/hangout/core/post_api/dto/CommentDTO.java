package com.hangout.core.post_api.dto;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

public record CommentDTO(UUID commentId, Instant createdAt, String text, BigInteger userId, BigInteger replyCount) {

}
