package com.emil.linksy_user.model;

import com.emil.linksy_user.util.ChannelType;
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
    private ChannelType type;
    private Boolean confirmed;
}
