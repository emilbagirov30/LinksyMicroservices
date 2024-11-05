package com.emil.linksy_auth.service;

import com.emil.linksy_auth.exception.InvalidVerificationCodeException;
import com.emil.linksy_auth.model.EmailRequest;
import com.emil.linksy_auth.model.User;
import com.emil.linksy_auth.exception.UserAlreadyExistsException;
import com.emil.linksy_auth.repository.UserRepository;
import com.emil.linksy_auth.util.CodeGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final KafkaTemplate<String, EmailRequest> kafkaTemplate;
    private final Map<String, User> pendingUsers = new HashMap<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public UserService(UserRepository userRepository, KafkaTemplate<String, EmailRequest> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void registerUser(String username, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с таким email уже существует");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setAvatarUrl("");
        pendingUsers.put(email, user);
        sendCode(email);
    }

    private EmailRequest getEmailRequest (String email){
        String code = CodeGenerator.generate(email);
        return new EmailRequest( email,"Confirmation code", "Your email verification code: " + code + ".\n"
                + "Do not share it with anyone!");
    }

    public void sendCode (String email){
        kafkaTemplate.send("emails", getEmailRequest(email));
    }

    public void confirmCode(String email, String code) {
        User user = pendingUsers.get(email);
        if (user == null || !CodeGenerator.isValidCode(email,code)) {
            throw new InvalidVerificationCodeException("Неверный код подтверждения");
        }
        userRepository.save(user);
        pendingUsers.remove(email);
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
