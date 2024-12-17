package com.emil.linksy_user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Long postId;
    private String authorUsername;
    private String authorAvatarUrl;
    private String imageUrl;
    private String videoUrl;
    private String audioUrl;
    private String voiceUrl;
    private String text;
    private String publishDate;
    private int likesCount;
    private int repostsCount;
}