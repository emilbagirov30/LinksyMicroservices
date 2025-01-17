package com.emil.linksy_cloud.service;

import com.emil.linksy_cloud.model.*;
import com.emil.linksy_cloud.util.ChannelType;
import com.emil.linksy_cloud.util.LinksyTools;
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
    private final KafkaTemplate<String, ChannelKafkaResponse> kafkaChannelTemplate;
    private final KafkaTemplate<String, ChannelPostKafkaResponse> kafkaChannelPostTemplate;
    public void produceAvatar(Long id, MultipartFile file) {
         byte[] fileBytes = getFileBytes(file);
         String avatarUrl = uploadResources(fileBytes,uploadImageDir,".png");
         sendMediaResponse(new MediaResponse(id,avatarUrl),Topic.AVATAR_RESPONSE);
    }


    public void produceGroup(List<Long>participantIds, MultipartFile avatar, String name) {
        String avatarUrl = "null";
        if (avatar!=null) {
            byte[] fileBytes = getFileBytes(avatar);
            avatarUrl = uploadResources(fileBytes, uploadImageDir, ".png");
        }

        sendGroupResponse(new GroupKafkaResponse(participantIds,avatarUrl,name.substring(1,name.length()-1)));
    }





    public void produceChannel(Long ownerId, String name, String link, String description, ChannelType type, MultipartFile avatar) {
        String channelName = LinksyTools.clearQuotes(name);
        String channelLink = LinksyTools.clearQuotes(link);
        String channelDescription = LinksyTools.clearQuotes(description);
        if (channelLink.isEmpty()) channelLink=null;
        if (channelDescription.isEmpty()) channelDescription=null;
        String avatarUrl = "null";
        if (avatar!=null) {
            byte[] fileBytes = getFileBytes(avatar);
            avatarUrl = uploadResources(fileBytes, uploadImageDir, ".png");
        }
        sendChannelResponse(new ChannelKafkaResponse(ownerId,channelName,channelLink,channelDescription,type,avatarUrl));
    }






    public void producePost(Long id,Long postId, String text, MultipartFile image, MultipartFile video,
                            MultipartFile audio, MultipartFile voice,String oldImageUrl,String oldVideoUrl,String oldAudioUrl,String oldVoiceUrl) {
        String textPost = LinksyTools.clearQuotes(text);
        if (textPost.isEmpty()) textPost=null;
        String imageUrl= oldImageUrl==null ? null : LinksyTools.clearQuotes(oldImageUrl);
        String videoUrl= oldVideoUrl==null ? null :             LinksyTools.clearQuotes(oldVideoUrl);
        String audioUrl= oldAudioUrl==null ? null :LinksyTools.clearQuotes(oldAudioUrl);
        String voiceUrl= oldVoiceUrl==null ? null :LinksyTools.clearQuotes(oldVoiceUrl);

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
        sendPostResponse(new PostKafkaResponse(id,postId,textPost,imageUrl,videoUrl,audioUrl,voiceUrl));
    }


    public void produceMessage(Long senderId, Long recipientId, String text, MultipartFile image, MultipartFile video,
                               MultipartFile audio, MultipartFile voice) throws InterruptedException {
        String textMessage = LinksyTools.clearQuotes(text);
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

    public void produceMoment(Long id, MultipartFile image, MultipartFile video,
                              MultipartFile audio, String text) {
        String textMoment = LinksyTools.clearQuotes(text);
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




    public void produceChannelPost(Long ownerId,Long channelId, String text, MultipartFile image, MultipartFile video,
                            MultipartFile audio,String pollTitle,List<String> options) {
        String textPost = LinksyTools.clearQuotes(text);
        if (textPost.isEmpty()) textPost = null;
        String title = LinksyTools.clearQuotes(pollTitle);
        if(title.isEmpty()) title=null;
        String imageUrl = null;
        String videoUrl= null;
        String audioUrl= null;
        List<String> optionsList = null;
         if(options!=null) {
             optionsList = options.stream().map(LinksyTools::clearQuotes).toList();
         }
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
        sendChannelPostResponse(new ChannelPostKafkaResponse(ownerId,channelId,textPost,imageUrl,videoUrl,audioUrl,title,optionsList));
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

    public void sendChannelResponse (ChannelKafkaResponse response){
        kafkaChannelTemplate.send(Topic.CHANNEL_RESPONSE.getTopic(),response);
    }
    public void sendChannelPostResponse (ChannelPostKafkaResponse response){
        kafkaChannelPostTemplate.send(Topic.CHANNEL_POST_RESPONSE.getTopic(),response);
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
