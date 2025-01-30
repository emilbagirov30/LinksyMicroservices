package com.emil.linksy_user.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLogin {
    private String email;
    private String password;
}
