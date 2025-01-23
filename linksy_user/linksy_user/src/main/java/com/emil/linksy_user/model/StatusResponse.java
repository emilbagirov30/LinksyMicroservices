package com.emil.linksy_user.model;

import com.emil.linksy_user.util.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusResponse {
    private String name;
    private MessageStatus status;
}
