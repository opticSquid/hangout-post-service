package com.hangout.core.post_api.projections;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

public interface FetchCommentProjection {
    UUID getCommentid();

    Instant getCreatedat();

    String getText();

    BigInteger getUserid();

    BigInteger getReplies();

}
