package com.emil.linksy_user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelPostResponse {
    private Long postId;
    private Long channelId;
    private String channelName;
    private Boolean confirmed;
    private String channelAvatarUrl;
    private String text;
    private String imageUrl;
    private String videoUrl;
    private String audioUrl;
    private String publishDate;
    private String pollTitle;
    private Boolean isVoted;
    private List<OptionResponse> options;
    private Double averageRating;
    private Boolean edited;
    private Long authorId;
    private Long commentsCount;
    private Integer userScore;
}
