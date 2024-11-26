package com.emil.linksy_user.controller;

import com.emil.linksy_user.exception.UserNotFoundException;
import com.emil.linksy_user.service.MediaService;
import com.emil.linksy_user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/users/upload")
public class CloudController {
    private final MediaService mediaService;

    public CloudController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/avatar")
    public ResponseEntity<Void> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
         Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         CompletableFuture<Boolean> future = mediaService.requestAvatarUpload(userId, file);
         try {
            boolean success = future.get();
            if (success) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Void> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
    }

}