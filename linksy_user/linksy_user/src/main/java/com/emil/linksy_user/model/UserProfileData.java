package com.emil.linksy_user.model;

import lombok.*;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserProfileData {
    private Long id;
    private String username;
    private String link;
    private String avatarUrl;
    private Boolean confirmed;
}
