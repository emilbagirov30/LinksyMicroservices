package com.emil.linksy_user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelResponse {
    private Long channelId;
    private Long ownerId;
    private String name;
    private String link;
    private String avatarUrl;
    private Double rating;
    private String type;
}
