package com.emil.linksy_user.model;

import com.emil.linksy_user.util.MessageStatus;
import lombok.Data;

@Data
public class Status {
    private Long chatId;
    private Long userId;
    MessageStatus status;
}
