package com.emil.linksy_user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public class ChangePassword {
        private String oldPassword;
        private String newPassword;
}
