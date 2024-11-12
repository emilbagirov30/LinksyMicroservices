package com.emil.linksy_user.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.Base64;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Value("${image.upload-dir}")
    private String uploadDir;

    @Value("${app.domain}")
    private String domain;

    @PostMapping("/upload")
    public String uploadAvatar(@RequestBody String base64Avatar) throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Avatar);
        String uniqueFileName = UUID.randomUUID() + ".png";
        File imageFile = new File(uploadDir, uniqueFileName);
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            fos.write(decodedBytes);
        }
        String url = domain + "uploads/images/" + uniqueFileName;
        return url;
    }
}