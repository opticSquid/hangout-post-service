package com.hangout.core.post_api.projections;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

public interface FetchCommentProjection {
    UUID getCommentId();

    Instant getCreatedAt();

    String getText();

    BigInteger getUserId();

    BigInteger getReplies();

}
