package com.emil.linksy_user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class AllUserData {
    private String username;
    private String avatarUrl;
    private String email;
    private Date birthday;
}