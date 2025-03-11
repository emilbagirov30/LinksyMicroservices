package com.emil.linksy_user.controller;

import com.emil.linksy_user.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(LinkAlreadyExistsException.class)
    public ResponseEntity<Void> handleLinkAlreadyExist(LinkAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<Void> handleInvalidVerificationCode(InvalidVerificationCodeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Void> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Void> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(BlockedException.class)
    public ResponseEntity<Void> handleBlockedException(BlockedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(BlockedException.class)
    public ResponseEntity<Void> handleInvalidToken(BlockedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> handleUserNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(BlacklistException.class)
    public ResponseEntity<String> handleBlacklistException(BlacklistException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
    @ExceptionHandler(BlockedException.class)
    public ResponseEntity<Void> handleUserBlockedException(BlockedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

}
