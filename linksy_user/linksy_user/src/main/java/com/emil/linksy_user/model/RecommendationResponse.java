package com.emil.linksy_user.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecommendationResponse {
    private Long channelId;
    private Long userId;
    private String avatarUrl;
    private String name;
    private String link;
    private Boolean confirmed;
}
