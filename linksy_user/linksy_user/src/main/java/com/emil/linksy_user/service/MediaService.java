package com.emil.linksy_user.service;

import com.emil.linksy_user.model.*;
import com.emil.linksy_user.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MediaService {
    private final UserService userService;
    @KafkaListener(topics = "avatarResponse", groupId = "group_id")
    public void consumeAvatar(MediaResponse mediaResponse) {
            userService.saveUserAvatar(mediaResponse);
    }
}
