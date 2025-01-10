package com.emil.linksy_cloud.service;

import com.emil.linksy_cloud.model.*;
import com.emil.linksy_cloud.util.Topic;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
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
    private final KafkaTemplate<String, MediaResponse> kafkaMediaTemplate;
    private final KafkaTemplate<String, PostKafkaResponse> kafkaPostTemplate;
    private final KafkaTemplate<String, MomentKafkaResponse> kafkaMomentTemplate;
    private final KafkaTemplate<String, MessageKafkaResponse> kafkaMessageTemplate;
    private final KafkaTemplate<String, GroupKafkaResponse> kafkaGroupTemplate;
    public void consumeAvatar(Long id, MultipartFile file) {
         byte[] fileBytes = getFileBytes(file);
         String avatarUrl = uploadResources(fileBytes,uploadImageDir,".png");
         sendMediaResponse(new MediaResponse(id,avatarUrl),Topic.AVATAR_RESPONSE);
    }








    public void consumeGroup(List<Long>participantIds, MultipartFile avatar,String name) {
        String avatarUrl = "null";
        if (avatar!=null) {
            byte[] fileBytes = getFileBytes(avatar);
            avatarUrl = uploadResources(fileBytes, uploadImageDir, ".png");
        }

        sendGroupResponse(new GroupKafkaResponse(participantIds,avatarUrl,name.substring(1,name.length()-1)));
    }











    public void consumePost(Long id, String text, MultipartFile image, MultipartFile video,
                            MultipartFile audio,MultipartFile voice) {
        String textPost = text.substring(1, text.length() - 1);
        if (textPost.isEmpty()) textPost=null;
        String imageUrl = null;
        String videoUrl= null;
        String audioUrl= null;
        String voiceUrl= null;

        if (image!=null) {
            byte[] imageBytes = getFileBytes(image);
            imageUrl = uploadResources(imageBytes,uploadImageDir,".png");
        }
        if (video!=null) {
            byte[] videoBytes = getFileBytes(video);
            videoUrl = uploadResources(videoBytes,uploadVideoDir,".mp4");
        }
        if (audio!=null){
            byte[] audioBytes = getFileBytes(audio);
            audioUrl = uploadResources(audioBytes,uploadAudioDir,".mp3");
        }
        if (voice!=null){
            byte[] voiceBytes = getFileBytes(voice);
            voiceUrl = uploadResources(voiceBytes,uploadVoiceDir,".mp3");
        }
        sendPostResponse(new PostKafkaResponse(id,textPost,imageUrl,videoUrl,audioUrl,voiceUrl));
    }


    public void consumeMessage(Long senderId,Long recipientId, String text, MultipartFile image, MultipartFile video,
                            MultipartFile audio,MultipartFile voice) throws InterruptedException {
        String textMessage = text.substring(1, text.length() - 1);
        if (textMessage.isEmpty()) textMessage=null;
        String imageUrl = null;
        String videoUrl= null;
        String audioUrl= null;
        String voiceUrl= null;

        if (image!=null) {
            byte[] imageBytes = getFileBytes(image);
            imageUrl = uploadResources(imageBytes,uploadImageDir,".png");
        }
        if (video!=null) {
            byte[] videoBytes = getFileBytes(video);
            videoUrl = uploadResources(videoBytes,uploadVideoDir,".mp4");
        }
        if (audio!=null){
            byte[] audioBytes = getFileBytes(audio);
            audioUrl = uploadResources(audioBytes,uploadAudioDir,".mp3");
        }
        if (voice!=null){
            byte[] voiceBytes = getFileBytes(voice);
            voiceUrl = uploadResources(voiceBytes,uploadVoiceDir,".mp3");
        }
        sendMessageResponse(new MessageKafkaResponse(senderId,recipientId,textMessage,imageUrl,videoUrl,audioUrl,voiceUrl));
    }

    public void consumeMoment(Long id, MultipartFile image, MultipartFile video,
                            MultipartFile audio,String text) {
        String textMoment = text.substring(1, text.length() - 1);
        if (textMoment.isEmpty()) textMoment=null;
        String imageUrl = null;
        String videoUrl= null;
        String audioUrl= null;

        if (image!=null) {
            byte[] imageBytes = getFileBytes(image);
            imageUrl = uploadResources(imageBytes,uploadImageDir,".png");
        }
        if (video!=null) {
            byte[] videoBytes = getFileBytes(video);
            videoUrl = uploadResources(videoBytes,uploadVideoDir,".mp4");
        }
        if (audio!=null){
            byte[] audioBytes = getFileBytes(audio);
            audioUrl = uploadResources(audioBytes,uploadAudioDir,".mp3");
        }

        sendMomentResponse(new MomentKafkaResponse(id,imageUrl,videoUrl,audioUrl,textMoment));
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



    public void sendMediaResponse(MediaResponse response, Topic topic){
         kafkaMediaTemplate.send(topic.getTopic(),response);
    }

    public void sendPostResponse (PostKafkaResponse response){
        kafkaPostTemplate.send(Topic.POST_RESPONSE.getTopic(),response);
    }
    public void sendMessageResponse (MessageKafkaResponse response){
        kafkaMessageTemplate.send(Topic.MESSAGE_RESPONSE.getTopic(),response);
    }
    public void sendMomentResponse (MomentKafkaResponse response){
        kafkaMomentTemplate.send(Topic.MOMENT_RESPONSE.getTopic(),response);
    }
    public void sendGroupResponse (GroupKafkaResponse response){
       kafkaGroupTemplate.send(Topic.GROUP_RESPONSE.getTopic(),response);
    }

private byte [] getFileBytes (MultipartFile file){
    byte[] fileBytes = null;
    try {
       return fileBytes = file.getBytes();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
}
