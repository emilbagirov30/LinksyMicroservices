package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.UserNotFoundException;
import com.emil.linksy_user.model.*;
import com.emil.linksy_user.repository.PostRepository;
import com.emil.linksy_user.util.Topic;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MediaService {
    private final KafkaTemplate<String, MediaRequest> kafkaAvatarTemplate;
    private final UserService userService;
    private final PostRepository postRepository;
    private final ConcurrentHashMap<Long, CompletableFuture<Boolean>> uploadStatus = new ConcurrentHashMap<>();
    private final Map<Long, Object> postLocks = new ConcurrentHashMap<>();

    @KafkaListener(topics = "avatarResponse", groupId = "group_id")
    public void consumeAvatar(MediaResponse mediaResponse) {
            userService.saveUserAvatar(mediaResponse);
    }


}
