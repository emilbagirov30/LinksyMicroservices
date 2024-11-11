package com.emil.linksy_user.controller;
import com.emil.linksy_user.exception.InvalidTokenException;
import com.emil.linksy_user.exception.InvalidVerificationCodeException;
import com.emil.linksy_user.exception.UserNotFoundException;
import com.emil.linksy_user.model.*;
import com.emil.linksy_user.exception.UserAlreadyExistsException;
import com.emil.linksy_user.security.JwtToken;
import com.emil.linksy_user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
class UserController {
    private final UserService userService;
    private final JwtToken jwtToken;
    public UserController(UserService userService, JwtToken jwtToken) {
        this.userService = userService;
        this.jwtToken = jwtToken;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRegistrationDto userDto) {
        userService.registerUser(userDto.getUsername(), userDto.getEmail(), userDto.getPassword());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<User> confirmCode(@RequestParam String email, @RequestParam String code) {
        userService.confirmCode(email, code);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend_code")
    public ResponseEntity<User> resendCode(@RequestParam String email) {
    userService.sendCodeToConfirmTheMail(email);
    return ResponseEntity.ok().build();
    }


    @PostMapping("/login")
    public ResponseEntity<Token> logIn(@RequestBody UserLogin userLogin) {
        Token tokens = userService.logIn(userLogin.getEmail(), userLogin.getPassword());
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<Token> refreshToken(@RequestParam String refreshToken) {
        Token tokens = userService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/request_password_change")
    public ResponseEntity<Void> requestPasswordChange(@RequestParam String email) {
        userService.requestPasswordChange(email);
        return ResponseEntity.ok().build(); // 200
    }

    @PostMapping("/confirm_password_change")
    public ResponseEntity<Void> confirmPasswordChange(@RequestBody ChangePassword changePassword) {
        userService.confirmPasswordChange(changePassword.getEmail(), changePassword.getCode(), changePassword.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Void> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
    }
    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<Void> handleInvalidVerificationCode(InvalidVerificationCodeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Void> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
    }
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Void> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
    }

}
