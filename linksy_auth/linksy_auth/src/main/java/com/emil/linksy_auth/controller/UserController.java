package com.emil.linksy_auth.controller;
import com.emil.linksy_auth.exception.InvalidVerificationCodeException;
import com.emil.linksy_auth.model.User;
import com.emil.linksy_auth.exception.UserAlreadyExistsException;
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
        User confirmedUser = userService.confirmCode(email, code);
        return ResponseEntity.ok(confirmedUser);
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
