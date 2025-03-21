package com.emil.linksy_user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Long postId;
    private Long authorId;
    private String authorUsername;
    private Boolean confirmed;
    private String authorAvatarUrl;
    private String imageUrl;
    private String videoUrl;
    private String audioUrl;
    private String voiceUrl;
    private String text;
    private String publishDate;
    private Long likesCount;
    private Long commentsCount;
    private Boolean isLikedIt;
    private Boolean edited;
}