package com.emil.linksy_user.service;

import com.emil.linksy_user.model.AvatarRequest;
import com.emil.linksy_user.model.AvatarResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MediaService {
    private final KafkaTemplate<String, AvatarRequest> kafkaAvatarTemplate;
    private final UserService userService;
    private final ConcurrentHashMap<Long, CompletableFuture<Boolean>> uploadStatus = new ConcurrentHashMap<>();
    @KafkaListener(topics = "avatarResponse", groupId = "group_id")
    public void consume(AvatarResponse avatarResponse) {
        Long userId = avatarResponse.getUserId();
        try {
            userService.saveUserAvatar(avatarResponse);
            uploadStatus.get(userId).complete(true);
        } catch (Exception e) {
            uploadStatus.get(userId).complete(false);
        } finally {
            uploadStatus.remove(userId);
        }
    }

        public CompletableFuture<Boolean> requestAvatarUpload (Long userId, MultipartFile file) {
        byte[] fileBytes = null;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
            AvatarRequest avatarRequest = new AvatarRequest(userId,fileBytes);
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            uploadStatus.put(userId, future);
            kafkaAvatarTemplate.send("avatarRequest", avatarRequest);
            return future;
        }


}
