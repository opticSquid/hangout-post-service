package com.hangout.core.post_api.dto;

import java.util.UUID;

public record Reply(UUID postId, UUID parentCommentId, String comment) {

}
