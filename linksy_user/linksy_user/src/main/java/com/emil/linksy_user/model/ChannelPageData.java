package com.emil.linksy_user.model;

import com.emil.linksy_user.util.ChannelType;
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
    private Boolean isSubmitted;
    private Double rating;
    private ChannelType type;
    private Long subscribersCount;
    private Long requestsCount;
    private Boolean confirmed;
}
