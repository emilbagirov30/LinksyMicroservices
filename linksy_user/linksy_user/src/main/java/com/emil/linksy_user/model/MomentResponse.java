package com.emil.linksy_user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MomentResponse {
    private Long momentId;
    private Long authorId;
    private String authorUsername;
    private String authorAvatarUrl;
    private String imageUrl;
    private String videoUrl;
    private String audioUrl;
    private String text;
    private String publishDate;
    private Boolean confirmed;
}