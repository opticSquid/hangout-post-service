package com.hangout.core.post_api.dto;

import java.math.BigInteger;

public record Session(BigInteger userId, String role, Boolean trustedDevice) {
}