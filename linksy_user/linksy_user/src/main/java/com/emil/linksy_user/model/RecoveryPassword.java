package com.emil.linksy_user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecoveryPassword {
    private String email;
    private String newPassword;
    private String code;
}