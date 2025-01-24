package com.hangout.core.post_api.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.hangout.core.post_api.dto.ActionType;
import com.hangout.core.post_api.dto.event.HeartEvent;
import com.hangout.core.post_api.entities.Heart;
import com.hangout.core.post_api.entities.Post;
import com.hangout.core.post_api.repositories.HeartRepo;
import com.hangout.core.post_api.repositories.PostRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HeartServiceKafkaConsumer {
    private final HeartRepo heartRepo;
    private final PostRepo postRepo;
    private final PostService postService;

    @KafkaListener(topics = "${hangout.kafka.heart.topic}", groupId = "${spring.application.name}")
    public void consumeHeartEvent(HeartEvent heartEvent) {
        if (heartEvent.actionType() == ActionType.ADD) {
            addHeart(heartEvent);
        } else {
            removeHeart(heartEvent);
        }
    }

    @Transactional
    private void addHeart(HeartEvent heartEvent) {
        Post post = postService.getParticularPost(heartEvent.postId());
        if (post != null) {
            Heart heart = new Heart(post, heartEvent.userId());
            postRepo.increaseHeartCount(post.getPostId());
            heartRepo.save(heart);
        }
    }

    @Transactional
    private void removeHeart(HeartEvent heartEvent) {
        Post post = postService.getParticularPost(heartEvent.postId());
        if (post != null) {
            postRepo.decreaseHeartCount(post.getPostId());
            heartRepo.removeHeart(post.getPostId(), heartEvent.userId());
        }
    }
}
