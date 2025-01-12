package com.emil.linksy_user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelPageData {
    private Long channelId;
    private Long ownerId;
    private String name;
    private String link;
    private String avatarUrl;
    private String description;
    private Boolean isSubscriber;
    private Double rating;
    private String type;
    private Long subscribersCount;
}
