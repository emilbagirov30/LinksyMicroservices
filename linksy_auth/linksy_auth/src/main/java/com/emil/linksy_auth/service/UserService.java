package com.emil.linksy_auth.service;

import com.emil.linksy_auth.exception.InvalidVerificationCodeException;
import com.emil.linksy_auth.model.EmailRequest;
import com.emil.linksy_auth.model.User;
import com.emil.linksy_auth.exception.UserAlreadyExistsException;
import com.emil.linksy_auth.repository.UserRepository;
import com.emil.linksy_auth.util.CodeGenerator;
import org.springframework.stereotype.Service;

import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final KafkaTemplate<String, EmailRequest> kafkaTemplate;
    private final Map<String, User> pendingUsers = new HashMap<>();
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
        user.setPassword(password);
        user.setAvatarUrl("");

        String code = CodeGenerator.generate(email);
        pendingUsers.put(email, user);

        EmailRequest emailRequest = new EmailRequest(email, "Код подтверждения Linksy: ", code);
        kafkaTemplate.send("emails", emailRequest);

    }
    public User confirmCode(String email, String code) {
        User user = pendingUsers.get(email);
        if (user == null || !CodeGenerator.isValidCode(email,code)) {
            throw new InvalidVerificationCodeException("Неверный код подтверждения");
        }
        User savedUser = userRepository.save(user);
        pendingUsers.remove(email);
        CodeGenerator.removeCode(email);
        return savedUser;
    }
}
