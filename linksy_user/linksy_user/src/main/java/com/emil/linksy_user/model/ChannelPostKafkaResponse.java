package com.emil.linksy_user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelPostKafkaResponse {
    private Long ownerId;
    private Long channelId;
    private String text;
    private String imageUrl;
    private String videoUrl;
    private String audioUrl;
    private String pollTitle;
    private List<String> options;
    private Long postId;
}
