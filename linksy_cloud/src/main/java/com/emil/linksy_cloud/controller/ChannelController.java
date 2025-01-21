package com.emil.linksy_cloud.controller;

import com.emil.linksy_cloud.service.MediaService;
import com.emil.linksy_cloud.util.ChannelType;
import com.emil.linksy_cloud.util.LinksyTools;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/channels")
public class ChannelController {
    private final MediaService mediaService;

    public ChannelController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/cu")
    public ResponseEntity<Void> createChannel(@RequestParam("name") String name,
                                              @RequestParam(value = "channelId", required = false) Long channelId,
                                              @RequestParam("link") String link,
                                              @RequestParam("description") String description,
                                              @RequestParam("type") String type,
                                              @RequestParam(value = "oldAvatarUrl", required = false) String oldAvatarUrl,
                                              @RequestParam(value = "image", required = false) MultipartFile avatar) {

        Long ownerId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ChannelType channelType =  (LinksyTools.clearQuotes(type).equals("PRIVATE")) ? ChannelType.PRIVATE : ChannelType.PUBLIC;
        mediaService.produceChannel(ownerId, channelId,name, link, description, channelType,oldAvatarUrl, avatar);
        return ResponseEntity.ok().build();
    }



    @PostMapping("/cu/post")
    public ResponseEntity<Void> createOrUpdatePost(  @RequestParam("channelId") Long channelId,
                                                     @RequestParam(value ="text",required = false) String text,
                                             @RequestParam(value ="postId",required = false) Long postId,
                                             @RequestParam(value = "imageUrl",required = false) String imageUrl,
                                             @RequestParam(value ="videoUrl",required = false) String videoUrl,
                                             @RequestParam(value ="audioUrl",required = false) String audioUrl,
                                             @RequestParam(value = "image", required = false) MultipartFile image,
                                             @RequestParam(value = "video", required = false) MultipartFile video,
                                             @RequestParam(value = "audio", required = false) MultipartFile audio,
                                             @RequestParam(value = "title", required = false) String pollTitle,
                                             @RequestParam(value = "options", required = false) List<String> options
    ) {
        Long ownerId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        mediaService.produceChannelPost(ownerId,channelId,text,image,video,audio,pollTitle,options,postId,imageUrl,videoUrl,audioUrl);
        return ResponseEntity.ok().build();
    }

}
