package com.emil.linksy_cloud.controller;

import com.emil.linksy_cloud.exception.UserNotFoundException;
import com.emil.linksy_cloud.service.MediaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/upload")
public class CloudController {
    private final MediaService mediaService;

    public CloudController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/avatar")
    public ResponseEntity<Void> uploadAvatar(@RequestParam(value = "image") MultipartFile avatar)  {
         Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         mediaService.produceAvatar(userId,avatar);
         return ResponseEntity.ok().build();
    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Void> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
    }

}