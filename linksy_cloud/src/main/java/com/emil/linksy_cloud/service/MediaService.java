package com.emil.linksy_cloud.service;

import com.emil.linksy_cloud.util.Topic;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import com.emil.linksy_cloud.model.MediaRequest;
import com.emil.linksy_cloud.model.MediaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class MediaService {
    @Value("${image.upload.image-dir}")
    private String uploadImageDir;
    @Value("${image.upload.video-dir}")
    private String uploadVideoDir;
    @Value("${image.upload.audio-dir}")
    private String uploadAudioDir;
    @Value("${image.upload.voice-dir}")
    private String uploadVoiceDir;
    @Value("${app.domain}")
    private String domain;
    private final KafkaTemplate<String, MediaResponse> kafkaTemplate;


    @KafkaListener(topics = "avatarRequest", groupId = "group_id")
    public void consumeAvatar(MediaRequest mediaRequest) {
         String avatarUrl = uploadResources(mediaRequest.getFileBytes(),uploadImageDir,".png");
         sendResponse(new MediaResponse(mediaRequest.getId(),avatarUrl),Topic.AVATAR_RESPONSE);
    }
    @KafkaListener(topics = "imageRequest", groupId = "group_id")
    public void consumeImage(MediaRequest mediaRequest) {
        String imageUrl = uploadResources(mediaRequest.getFileBytes(),uploadImageDir,".png");
        sendResponse(new MediaResponse(mediaRequest.getId(), imageUrl),Topic.IMAGE_POST_RESPONSE);
    }
    @KafkaListener(topics = "videoRequest", groupId = "group_id")
    public void consumeVideo(MediaRequest mediaRequest) {
        String videoUrl = uploadResources(mediaRequest.getFileBytes(),uploadVideoDir,".mp4");
        sendResponse(new MediaResponse(mediaRequest.getId(), videoUrl),Topic.VIDEO_POST_RESPONSE);
    }

    @KafkaListener(topics = "audioRequest", groupId = "group_id")
    public void consumeAudio(MediaRequest mediaRequest) {
        String audioUrl = uploadResources(mediaRequest.getFileBytes(),uploadAudioDir,".mp3");
        sendResponse(new MediaResponse(mediaRequest.getId(),audioUrl),Topic.AUDIO_POST_RESPONSE);
    }
    @KafkaListener(topics = "voiceRequest", groupId = "group_id")
    public void consumeVoice(MediaRequest mediaRequest) {
        String voiceUrl = uploadResources(mediaRequest.getFileBytes(),uploadVoiceDir,".mp3");
        sendResponse(new MediaResponse(mediaRequest.getId(), voiceUrl),Topic.VOICE_POST_RESPONSE);
    }


    public String uploadResources( byte[] fileBytes,String dir,String ext)  {
        String uniqueFileName = UUID.randomUUID().toString() + UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + ext;
        File directory = new File(dir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File imageFile = new File( directory, uniqueFileName);
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            fos.write(fileBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return domain + dir + uniqueFileName;
    }



    public void sendResponse (MediaResponse response, Topic topic){
         kafkaTemplate.send(topic.getTopic(),response);
    }

}
