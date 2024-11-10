package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.InvalidVerificationCodeException;
import com.emil.linksy_user.exception.UserNotFoundException;
import com.emil.linksy_user.exception.UserAlreadyExistsException;
import com.emil.linksy_user.model.EmailRequest;
import com.emil.linksy_user.model.User;
import com.emil.linksy_user.repository.UserRepository;
import com.emil.linksy_user.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final KafkaTemplate<String, EmailRequest> kafkaTemplate;
    private final Map<String, User> pendingUsers = new HashMap<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void registerUser(String username, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с таким email уже существует");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setAvatar_url("");
        pendingUsers.put(email, user);
        sendCodeToConfirmTheMail(email);
    }

    private EmailRequest getEmailRequest(String email, String body) {
        String code = CodeGenerator.generate(email);
        return new EmailRequest(email, "Confirmation code", body + code + ".\n" + "Do not share it with anyone!");
    }

    public void sendCodeToConfirmTheMail(String email) {
        kafkaTemplate.send("emails", getEmailRequest(email, "Your email verification code: "));
    }

    public void sendCodeToConfirmThePasswordChange(String email) {
        kafkaTemplate.send("emails", getEmailRequest(email, "Your password change code: "));
    }

    public void confirmCode(String email, String code) {
        User user = pendingUsers.get(email);
        if (user == null || !CodeGenerator.isValidCode(email, code)) {
            throw new InvalidVerificationCodeException("Неверный код подтверждения");
        }
        userRepository.save(user);
        pendingUsers.remove(email);
        CodeGenerator.removeCode(email);
    }

    public void requestPasswordChange(String email) {
        if (userRepository.findByEmail(email).isEmpty()) {
            throw new UserNotFoundException("Пользователь с таким email не зарегистрирован");
        }
        sendCodeToConfirmThePasswordChange(email);
    }

    public void confirmPasswordChange(String email, String code, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким email не найден"));
        if (!CodeGenerator.isValidCode(email, code)) {
            throw new InvalidVerificationCodeException("Неверный код подтверждения");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        CodeGenerator.removeCode(email);
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public boolean logIn(String email, String password) {
        return userRepository.findByEmail(email)
                .map(user -> validatePassword(password, user.getPassword()))
                .orElse(false);
    }
}
