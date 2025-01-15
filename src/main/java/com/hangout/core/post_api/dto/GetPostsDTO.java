package com.hangout.core.post_api.dto;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;

public record GetPostsDTO(Point<G2D> useLocation, Integer searchRadius) {

}
