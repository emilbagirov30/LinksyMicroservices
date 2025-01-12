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
@RequestMapping("/api/moments")
public class MomentController {
    private final MediaService mediaService;

    public MomentController(MediaService mediaService) {
        this.mediaService = mediaService;
    }
    @PostMapping("/create")
    public ResponseEntity<Void> createMoment(
                                           @RequestParam(value = "image", required = false) MultipartFile image,
                                           @RequestParam(value = "video", required = false) MultipartFile video,
                                           @RequestParam(value = "audio", required = false) MultipartFile audio,
                                           @RequestParam("text") String text) {
        Long authorId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        mediaService.produceMoment(authorId,image,video,audio,text);
        return ResponseEntity.ok().build();
    }

}