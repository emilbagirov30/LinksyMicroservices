package com.emil.linksy_user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private long id;
    private String avatarUrl;
    private String username;
    private String link;
}
