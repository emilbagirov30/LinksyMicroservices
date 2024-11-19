package com.emil.linksy_user.controller;

import com.emil.linksy_user.exception.UserNotFoundException;
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
import java.util.Base64;

@RestController
@RequestMapping("/api/users/upload")
public class ImageController {
    private final UserService userService;

    public ImageController(UserService userService) {
        this.userService = userService;
    }
    @Value("${image.upload-dir}")
    private String uploadDir;

    @Value("${app.domain}")
    private String domain;

    @PostMapping("/avatar")
    public ResponseEntity<Void> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uniqueFileName = UUID.randomUUID() + ".png";

        File imageFile = new File(uploadDir, uniqueFileName);
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            fos.write(file.getBytes());
        }
        String avatarUrl = domain + uploadDir + uniqueFileName;
        userService.uploadAvatar(userId,avatarUrl);
        return ResponseEntity.ok().build();
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Void> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
    }

}