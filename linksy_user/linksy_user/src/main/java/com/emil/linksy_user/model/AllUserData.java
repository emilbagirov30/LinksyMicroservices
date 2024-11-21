package com.emil.linksy_user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AllUserData {
    private String username;
    private String avatarUrl;
    private String email;
    private String link;
    private String birthday;
}