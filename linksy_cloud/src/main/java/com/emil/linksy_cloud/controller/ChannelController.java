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

    @PostMapping("/create")
    public ResponseEntity<Void> createChannel(@RequestParam("name") String name,
                                              @RequestParam("link") String link,
                                              @RequestParam("description") String description,
                                              @RequestParam("type") String type,
                                              @RequestParam(value = "image", required = false) MultipartFile avatar) {

        Long ownerId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ChannelType channelType =  (LinksyTools.clearQuotes(type).equals("PRIVATE")) ? ChannelType.PRIVATE : ChannelType.PUBLIC;
        mediaService.produceChannel(ownerId, name, link, description, channelType, avatar);
        return ResponseEntity.ok().build();
    }



    @PostMapping("/create_post")
    public ResponseEntity<Void> createPost(  @RequestParam("id") Long channelId,
                                             @RequestParam("text") String text,
                                             @RequestParam(value = "image", required = false) MultipartFile image,
                                             @RequestParam(value = "video", required = false) MultipartFile video,
                                             @RequestParam(value = "audio", required = false) MultipartFile audio,
                                             @RequestParam("title") String pollTitle,
                                             @RequestParam(value = "options", required = false) List<String> options
    ) {
        Long ownerId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        mediaService.produceChannelPost(ownerId,channelId,text,image,video,audio,pollTitle,options);
        return ResponseEntity.ok().build();
    }

}
