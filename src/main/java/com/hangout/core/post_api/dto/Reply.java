package com.hangout.core.post_api.dto;

import java.util.UUID;

public record Reply(UUID parentCommentId, String comment) {

}
