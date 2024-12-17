package com.emil.linksy_user.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private String text;
    private MultipartFile image;
    private MultipartFile video;
    private MultipartFile audio;
    private MultipartFile voice;
}
