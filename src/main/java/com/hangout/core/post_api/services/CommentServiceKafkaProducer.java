package com.hangout.core.post_api.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.hangout.core.post_api.dto.DefaultResponse;
import com.hangout.core.post_api.dto.NewCommentRequest;
import com.hangout.core.post_api.dto.Reply;
import com.hangout.core.post_api.dto.Session;
import com.hangout.core.post_api.dto.UserValidationRequest;
import com.hangout.core.post_api.dto.event.CommentEvent;
import com.hangout.core.post_api.exceptions.UnauthorizedAccessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceKafkaProducer {
    private final RestClient restClient;
    private final KafkaTemplate<UUID, CommentEvent> kafkaTemplate;
    @Value("${hangout.auth-service.url}")
    private String authServiceURL;
    @Value("${hangout.kafka.comment.topic}")
    private String commentTopic;

    public DefaultResponse createTopLevelComment(String authToken, NewCommentRequest comment) {
        Session session = authorizeUser(authToken);
        if (session.userId() != null) {
            kafkaTemplate.send(commentTopic,
                    new CommentEvent(comment.postId(), session.userId(), Optional.empty(), comment.comment()));
            return new DefaultResponse("comment posted");
        } else {
            return new DefaultResponse("user not authorized, can not post comment");
        }
    }

    public DefaultResponse createSubComments(String authToken, Reply reply) {
        Session session = authorizeUser(authToken);
        if (session.userId() != null) {
            kafkaTemplate.send(commentTopic,
                    new CommentEvent(reply.postId(), session.userId(), Optional.of(reply.parentCommentId()),
                            reply.comment()));
            return new DefaultResponse("comment posted");
        } else {
            return new DefaultResponse("user not authorized, can not post comment");
        }
    }

    private Session authorizeUser(String authHeader) {
        ResponseEntity<Session> response = restClient
                .post()
                .uri(authServiceURL + "/auth-api/v1/internal/validate")
                .body(new UserValidationRequest(authHeader))
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
