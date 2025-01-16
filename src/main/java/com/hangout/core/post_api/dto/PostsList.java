package com.hangout.core.post_api.dto;

import java.util.List;
import java.util.Optional;

public record PostsList(List<GetNearbyPostsProjection> posts, Optional<Integer> totalCount) {

}
