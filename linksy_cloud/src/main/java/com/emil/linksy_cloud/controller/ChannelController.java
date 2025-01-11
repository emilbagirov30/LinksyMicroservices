package com.emil.linksy_cloud.controller;

import com.emil.linksy_cloud.service.MediaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
        mediaService.consumeChannel(ownerId, name, link, description, type, avatar);
        return ResponseEntity.ok().build();
    }
}
