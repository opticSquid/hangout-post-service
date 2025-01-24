package com.hangout.core.post_api.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.hangout.core.post_api.dto.HasHearted;
import com.hangout.core.post_api.dto.Session;
import com.hangout.core.post_api.dto.UserValidationRequest;
import com.hangout.core.post_api.exceptions.UnauthorizedAccessException;
import com.hangout.core.post_api.repositories.HeartRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HeartService {
    private final RestClient restClient;
    @Value("${hangout.auth-service.url}")
    private String authServiceURL;
    private final HeartRepo heartRepo;

    @Transactional
    public HasHearted hasHearted(String authToken, UUID postId) {
        Session session = authorizeUser(authToken);
        if (session.userId() != null) {
            return new HasHearted(heartRepo.hasHearted(postId, session.userId()));
        } else {
            throw new UnauthorizedAccessException("User is not authorized");
        }
    }

    private Session authorizeUser(String authToken) {
        ResponseEntity<Session> response = restClient
                .post()
                .uri(authServiceURL + "/auth-api/v1/internal/validate")
                .body(new UserValidationRequest(authToken))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(Session.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new UnauthorizedAccessException(
                    "User is not valid or user does not have permission to perform current action");
        }
    }
}
