package com.emil.linksy_user.controller;
import com.emil.linksy_user.exception.*;
import com.emil.linksy_user.model.*;
import com.emil.linksy_user.service.UserService;
import com.emil.linksy_user.util.MessageMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/users")
class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;

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

    @PatchMapping("/refresh_token")
    public ResponseEntity<Token> refreshToken(@RequestParam String refreshToken) {
        Token tokens = userService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(tokens);
    }

    @GetMapping("/profile_data")
    public ResponseEntity<UserProfileData> getUserProfileData() throws InterruptedException {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserProfileData userProfileData = userService.getUserProfileData(userId);
        return ResponseEntity.ok(userProfileData);
    }
    @GetMapping("/all_data")
    public ResponseEntity<AllUserData> getAllUserData() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AllUserData allUserData = userService.getAllUserData(userId);
        return ResponseEntity.ok(allUserData);
    }

    @PostMapping("/request_password_change")
    public ResponseEntity<Void> requestPasswordChange(@RequestParam String email) {
        userService.requestPasswordChange(email);
        return ResponseEntity.ok().build(); // 200
    }

    @PostMapping("/confirm_password_change")
    public ResponseEntity<Void> confirmPasswordChange(@RequestBody RecoveryPassword recoveryPassword) {
        userService.confirmPasswordChange(recoveryPassword.getEmail(), recoveryPassword.getCode(), recoveryPassword.getNewPassword());
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/update_birthday")
    public ResponseEntity<Void> updateBirthday(@RequestParam String birthday) throws ParseException {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.updateBirthday(userId,birthday);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/update_username")
    public ResponseEntity<Void> updateUsername(@RequestParam String username) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.updateUsername(userId,username);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/update_link")
    public ResponseEntity<Void> updateLink(@RequestParam String link) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.updateLink(userId,link);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/delete_avatar")
    public ResponseEntity<Void> deleteAvatar() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.deleteAvatar(userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/change_password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePassword changePassword) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.changePassword(userId,changePassword);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/message_mode")
    public ResponseEntity<MessageMode> getMessageMode() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var mode = userService.getUserMessageMode(userId);
        return ResponseEntity.ok(mode);
    }

    @PutMapping("/message_mode")
    public ResponseEntity<Void> setMessageMode(@RequestBody MessageMode messageMode) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.setMessageMode(userId,messageMode);
        return ResponseEntity.ok().build();
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> handleUserNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
    }
    @ExceptionHandler(LinkAlreadyExistsException.class)
    public ResponseEntity<Void> handleLinkAlreadyExist(LinkAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
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
