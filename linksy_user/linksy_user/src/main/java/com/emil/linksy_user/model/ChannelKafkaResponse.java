package com.emil.linksy_user.model;


import com.emil.linksy_user.util.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelKafkaResponse {
    private Long ownerId;
    private Long channelId;
    private String name;
    private String link;
    private String description;
    private ChannelType type;
    private String avatarUrl;
}
