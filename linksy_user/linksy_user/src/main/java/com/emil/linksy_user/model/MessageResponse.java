package com.emil.linksy_user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private Long messageId;
    private Long senderId;
    private Long chatId;
    private String text;
    private String imageUrl;
    private String videoUrl;
    private String audioUrl;
    private String voiceUrl;
    private String date;
}
