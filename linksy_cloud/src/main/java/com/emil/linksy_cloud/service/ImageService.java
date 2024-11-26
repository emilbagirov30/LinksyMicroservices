package com.emil.linksy_cloud.service;

import lombok.RequiredArgsConstructor;
import com.emil.linksy_cloud.model.AvatarRequest;
import com.emil.linksy_cloud.model.AvatarResponse;
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
public class ImageService {
    @Value("${image.upload-dir}")
    private String uploadDir;
    @Value("${app.domain}")
    private String domain;
    private final KafkaTemplate<String, AvatarResponse> kafkaTemplate;

    @KafkaListener(topics = "avatarRequest", groupId = "group_id")
    public void consume(AvatarRequest avatarRequest) {
         String avatarUrl = uploadAvatar(avatarRequest.getFileBytes());
         sendAvatarResponse(new AvatarResponse(avatarRequest.getUserId(),avatarUrl));
    }

    public String uploadAvatar( byte[] fileBytes)  {
        String uniqueFileName = UUID.randomUUID() + ".png";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File imageFile = new File( directory, uniqueFileName);
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            fos.write(fileBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return domain + uploadDir + uniqueFileName;
    }

    public void sendAvatarResponse (AvatarResponse response){
         kafkaTemplate.send("avatarResponse",response);
    }

}
