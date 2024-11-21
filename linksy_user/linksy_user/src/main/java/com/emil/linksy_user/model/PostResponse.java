package com.emil.linksy_user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private String authorUsername;
    private String authorAvatarUrl;
    private String text;
    private String publishDate;
    private int likesCount;
    private int repostsCount;
}