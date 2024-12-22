package com.hangout.core.post_api.dto;

import java.math.BigInteger;

public record User(BigInteger userId, String role, Boolean trustedDevice) {
}