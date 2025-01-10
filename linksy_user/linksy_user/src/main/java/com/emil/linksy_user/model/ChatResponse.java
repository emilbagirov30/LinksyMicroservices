package com.emil.linksy_user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private Long chatId;
    private Long companionId;
    private Boolean isGroup;
    private String avatarUrl;
    private String displayName;
    private String lastMessage;
    private String dateLast;
}
