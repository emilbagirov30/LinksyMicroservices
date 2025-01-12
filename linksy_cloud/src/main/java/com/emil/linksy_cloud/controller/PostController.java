package com.emil.linksy_cloud.controller;


import com.emil.linksy_cloud.service.MediaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final MediaService mediaService;

    public PostController(MediaService mediaService) {
        this.mediaService = mediaService;
    }
    @PostMapping("/create")
    public ResponseEntity<Void> createPost(  @RequestParam("text") String text,
                                             @RequestParam(value = "image", required = false) MultipartFile image,
                                             @RequestParam(value = "video", required = false) MultipartFile video,
                                             @RequestParam(value = "audio", required = false) MultipartFile audio,
                                             @RequestParam(value = "voice", required = false) MultipartFile voice) {
        Long authorId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        mediaService.producePost(authorId,text,image,video,audio,voice);
        return ResponseEntity.ok().build();
    }

}
