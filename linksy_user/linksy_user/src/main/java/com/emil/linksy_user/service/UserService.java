package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.*;
import com.emil.linksy_user.model.*;
import com.emil.linksy_user.repository.UserRepository;
import com.emil.linksy_user.security.JwtToken;
import com.emil.linksy_user.security.TokenType;
import com.emil.linksy_user.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final KafkaTemplate<String, EmailRequest> kafkaEmailTemplate;
    private final Map<String, User> pendingUsers = new HashMap<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtToken jwtToken;
    private final Map<Long, Object> userLocks = new ConcurrentHashMap<>();



    public void registerUser(String username, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с таким email уже существует");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setAvatarUrl("null");
        pendingUsers.put(email, user);
        sendCodeToConfirmTheMail(email);
    }

    private EmailRequest getEmailRequest(String email, String body) {
        String code = CodeGenerator.generate(email);
        return new EmailRequest(email, "Confirmation code", body + code + ".\n" + "Do not share it with anyone!");
    }

    public void sendCodeToConfirmTheMail(String email) {
        kafkaEmailTemplate.send("emails", getEmailRequest(email, "Your email verification code: "));
    }

    public void sendCodeToConfirmThePasswordChange(String email) {
        kafkaEmailTemplate.send("emails", getEmailRequest(email, "Your password change code: "));
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
            throw new NotFoundException("Пользователь с таким email не зарегистрирован");
        }
        sendCodeToConfirmThePasswordChange(email);
    }

    public void confirmPasswordChange(String email, String code, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким email не найден"));
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

    public Token logIn(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> validatePassword(password, user.getPassword()))
                .map(user -> {
                    String accessToken = jwtToken.generateAccessToken(user.getId().toString());
                    String refreshToken = jwtToken.generateRefreshToken(user.getId().toString());
                   return new Token(accessToken,refreshToken);
                })
                .orElseThrow(() -> new NotFoundException("Invalid email or password"));
    }

    public Token refreshAccessToken(String refreshToken) {
        if (!jwtToken.validateRefreshToken(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }
        String userId = String.valueOf(jwtToken.extractUserId(refreshToken, TokenType.REFRESH));
        userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new NotFoundException("User not found"));
        String newAccessToken = jwtToken.generateAccessToken(userId);
        String newRefreshToken = refreshToken;
        if (jwtToken.needsRefreshRenewal(refreshToken)) {
            newRefreshToken = jwtToken.generateRefreshToken(userId);
        }

        return new Token(newAccessToken, newRefreshToken);
    }

    public UserProfileData getUserProfileData(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        return new UserProfileData(userId,user.getUsername(),user.getLink(), user.getAvatarUrl());
    }

    public AllUserData getAllUserData(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String birthday = null;
        if (user.getBirthday()!=null)
          birthday = dateFormat.format(user.getBirthday());
        return new AllUserData(user.getUsername(), user.getAvatarUrl(),user.getEmail(),user.getLink(),birthday);
    }


    public void saveUserAvatar (MediaResponse response) {
        Long userId = response.getId();
        String avatarUrl = response.getUrl();
        synchronized (getUserLock(userId)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);
        }
    }

    public void updateUsername(Long userId, String newUsername) {
        synchronized (getUserLock(userId)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            user.setUsername(newUsername);
            userRepository.save(user);
        }
    }

    public void updateLink(Long userId, String link) {
        synchronized (getUserLock(userId)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            if (userRepository.existsByLinkAndIdNot(link, userId)) {
                throw new LinkAlreadyExistsException("Link is already in use by another user");
            }
            user.setLink(link);
            userRepository.save(user);
        }
    }

    public void updateBirthday(Long userId, String birthday) throws ParseException {
        synchronized (getUserLock(userId)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date newBirthday = dateFormat.parse(birthday);
            user.setBirthday(newBirthday);
            userRepository.save(user);
        }
    }

    public void deleteAvatar(Long userId) {
        synchronized (getUserLock(userId)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            user.setAvatarUrl("null");
            userRepository.save(user);
        }
    }
    public void changePassword(Long userId, ChangePassword changePassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        boolean correctPassword = validatePassword(changePassword.getOldPassword(), user.getPassword());
        if(correctPassword) {
            user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
            userRepository.save(user);
        }else throw new NotFoundException("Invalid password");

    }

    private Object getUserLock(Long userId) {
        userLocks.putIfAbsent(userId, new Object());
        return userLocks.get(userId);
    }


}

