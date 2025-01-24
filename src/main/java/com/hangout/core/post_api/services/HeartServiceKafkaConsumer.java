package com.hangout.core.post_api.services;

import java.util.ArrayList;
import java.util.List;

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

    @KafkaListener(topics = "${hangout.kafka.heart.topic}", groupId = "${spring.application.name}", containerFactory = "batchEventContainerFactory")
    public void consumeHeartEvent(List<HeartEvent> heartEvent) {
        List<HeartEvent> addHeartList = new ArrayList<>();
        List<HeartEvent> removeHeartList = new ArrayList<>();
        heartEvent.stream().forEach(h -> {
            if (h.actionType() == ActionType.ADD) {
                addHeartList.add(h);
            } else {
                removeHeartList.add(h);
            }
        });
        addHeart(addHeartList);
        removeHeart(removeHeartList);
    }

    @Transactional
    private void addHeart(List<HeartEvent> heartEvents) {
        List<Heart> heartList = new ArrayList<>();
        heartEvents.stream().forEach(h -> {
            Post post = postService.getParticularPost(h.postId());
            if (post != null) {
                Heart heart = new Heart(post, h.userId());
                // TODO: rather than calling this each time we can create a map<Post, like>
                // which can be used to count which post got how many likes and the like counter
                // for that post can be increased in one go
                postRepo.increaseHeartCount(post.getPostId());
                heartList.add(heart);
            }
        });
        heartRepo.saveAll(heartList);
    }

    @Transactional
    private void removeHeart(List<HeartEvent> heartEvents) {
        heartEvents.forEach(h -> {
            Post post = postService.getParticularPost(h.postId());
            if (post != null) {
                postRepo.decreaseHeartCount(post.getPostId());
                heartRepo.removeHeart(post.getPostId(), h.userId());
            }
        });
    }
}
