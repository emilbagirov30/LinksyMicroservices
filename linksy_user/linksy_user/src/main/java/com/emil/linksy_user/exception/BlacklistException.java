package com.emil.linksy_user.exception;

public class BlacklistException extends RuntimeException {
    public BlacklistException(String message) {
        super(message);
    }
}