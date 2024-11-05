package com.emil.linksy_auth.model;

public class ChangePassword {
    private String email;
    private String newPassword;
    private String code;

    public ChangePassword(String email, String newPassword, String code) {
        this.email = email;
        this.newPassword = newPassword;
        this.code = code;
    }
    public String getEmail() {
        return email;
    }
    public String getNewPassword() {
        return newPassword;
    }

    public String getCode() {
        return code;
    }
}
