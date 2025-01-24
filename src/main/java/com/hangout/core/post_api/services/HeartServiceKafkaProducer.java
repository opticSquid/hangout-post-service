package com.hangout.core.post_api.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.hangout.core.post_api.dto.ActionType;
import com.hangout.core.post_api.dto.DefaultResponse;
import com.hangout.core.post_api.dto.NewHeartRequest;
import com.hangout.core.post_api.dto.Session;
import com.hangout.core.post_api.dto.UserValidationRequest;
import com.hangout.core.post_api.dto.event.HeartEvent;
import com.hangout.core.post_api.exceptions.UnauthorizedAccessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HeartServiceKafkaProducer {
    private final RestClient restClient;
    private final KafkaTemplate<UUID, HeartEvent> kafkaTemplate;
    @Value("${hangout.auth-service.url}")
    private String authServiceURL;
    @Value("${hangout.kafka.heart.topic}")
    private String heartTopic;

    public DefaultResponse addHeart(String authToken, NewHeartRequest heartRequest) {
        Session session = authorizeUser(authToken);
        if (session.userId() != null) {
            kafkaTemplate.send(heartTopic,
                    new HeartEvent(ActionType.ADD, heartRequest.postId(), session.userId()));
            return new DefaultResponse("hearted post");
        } else {
            return new DefaultResponse("user not authorized can not heart post");
        }
    }

    public DefaultResponse removeHeart(String authToken, NewHeartRequest heartRequest) {
        Session session = authorizeUser(authToken);
        if (session.userId() != null) {
            kafkaTemplate.send(heartTopic,
                    new HeartEvent(ActionType.REMOVE, heartRequest.postId(), session.userId()));
            return new DefaultResponse("remvoed heart from post");
        } else {
            return new DefaultResponse("user not authorized can not remove heart from post");
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
