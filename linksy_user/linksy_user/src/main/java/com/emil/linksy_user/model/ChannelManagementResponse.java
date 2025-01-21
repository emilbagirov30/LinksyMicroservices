package com.emil.linksy_user.model;

import com.emil.linksy_user.util.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelManagementResponse {
    private String name;
    private String link;
    private String avatarUrl;
    private String description;
    private ChannelType type;
}
