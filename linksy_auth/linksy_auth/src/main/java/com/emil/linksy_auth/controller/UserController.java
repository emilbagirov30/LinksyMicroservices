package com.emil.linksy_auth.controller;
import com.emil.linksy_auth.exception.InvalidVerificationCodeException;
import com.emil.linksy_auth.exception.UserNotFoundException;
import com.emil.linksy_auth.model.ChangePassword;
import com.emil.linksy_auth.model.User;
import com.emil.linksy_auth.exception.UserAlreadyExistsException;
import com.emil.linksy_auth.model.UserLogin;
import com.emil.linksy_auth.model.UserRegistrationDto;
import com.emil.linksy_auth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Void> logIn(@RequestBody UserLogin userLogin) {
        boolean success = userService.logIn(userLogin.getEmail(), userLogin.getPassword());
        if (success) {
            return ResponseEntity.ok().build(); // 200
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }
    }

    @PostMapping("/request_password_change")
    public ResponseEntity<Void> requestPasswordChange(@RequestParam String email) {
        userService.requestPasswordChange(email);
        return ResponseEntity.ok().build(); // 200
    }

    @PostMapping("/confirm_password_change")
    public ResponseEntity<Void> confirmPasswordChange(@RequestBody ChangePassword changePassword) {
        userService.confirmPasswordChange(changePassword.getEmail(), changePassword.getCode(), changePassword.getNewPassword());
        return ResponseEntity.ok().build(); // 200
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
}
