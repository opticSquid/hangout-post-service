package com.hangout.core.post_api.dto;

public record GetPostsDTO(Double lat, Double lon, Double minSearchRadius, Double maxSearchRadius, Integer pageNumber) {

}
