package com.emil.linksy_user.service;

import com.emil.linksy_user.model.MediaRequest;
import com.emil.linksy_user.model.MediaResponse;
import com.emil.linksy_user.model.Post;
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
        Long userId = mediaResponse.getId();
        try {
            userService.saveUserAvatar(mediaResponse);
            uploadStatus.get(userId).complete(true);
        } catch (Exception e) {
            uploadStatus.get(userId).complete(false);
        } finally {
            uploadStatus.remove(userId);
        }
    }


    @KafkaListener(topics = "imageResponse", groupId = "group_id")
    public void consumeImage(MediaResponse mediaResponse) {
        Long postId = mediaResponse.getId();
        synchronized (getPostLock(postId)) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalCallerException("Пост не найден"));
            post.setImageUrl(mediaResponse.getUrl());
            postRepository.save(post);
        }
    }

    @KafkaListener(topics = "videoResponse", groupId = "group_id")
    public void consumeVideo(MediaResponse mediaResponse) {
        Long postId = mediaResponse.getId();
        synchronized (getPostLock(postId)) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalCallerException("Пост не найден"));
            post.setVideoUrl(mediaResponse.getUrl());
            postRepository.save(post);
        }
    }
    @KafkaListener(topics = "audioResponse", groupId = "group_id")
    public void consumeAudio(MediaResponse mediaResponse) {
        Long postId = mediaResponse.getId();
        synchronized (getPostLock(postId)) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalCallerException("Пост не найден"));
        post.setAudioUrl(mediaResponse.getUrl());
        postRepository.save(post);
        }
    }
    @KafkaListener(topics = "voiceResponse", groupId = "group_id")
    public void consumeVoice(MediaResponse mediaResponse) {
        Long postId = mediaResponse.getId();
        synchronized (getPostLock(postId)) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalCallerException("Пост не найден"));
            post.setVoiceUrl(mediaResponse.getUrl());
            postRepository.save(post);
        }
    }


    public CompletableFuture<Boolean> requestAvatarUpload (Long userId, MultipartFile file) {
        byte[] fileBytes = null;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
            MediaRequest mediaRequest = new MediaRequest(userId,fileBytes);
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            uploadStatus.put(userId, future);
            kafkaAvatarTemplate.send("avatarRequest", mediaRequest);
            return future;
        }


        public void requestPostResourcesUpload (Long postId, MultipartFile file, Topic topic){
            byte[] fileBytes = null;
            try {
                fileBytes = file.getBytes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            MediaRequest mediaRequest = new MediaRequest(postId,fileBytes);
            kafkaAvatarTemplate.send(topic.getTopic(), mediaRequest);

        }

    private Object getPostLock(Long postId) {
        postLocks.putIfAbsent(postId, new Object());
        return postLocks.get(postId);
    }



}
