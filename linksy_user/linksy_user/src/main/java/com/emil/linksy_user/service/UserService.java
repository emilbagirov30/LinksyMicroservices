package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.*;
import com.emil.linksy_user.model.*;
import com.emil.linksy_user.model.entity.Chat;
import com.emil.linksy_user.model.entity.ChatMember;
import com.emil.linksy_user.model.entity.Message;
import com.emil.linksy_user.model.entity.User;
import com.emil.linksy_user.repository.ChatMemberRepository;
import com.emil.linksy_user.repository.ChatRepository;
import com.emil.linksy_user.repository.MessageRepository;
import com.emil.linksy_user.repository.UserRepository;
import com.emil.linksy_user.security.JwtToken;
import com.emil.linksy_user.security.TokenType;
import com.emil.linksy_user.util.CodeGenerator;
import com.emil.linksy_user.util.LinksyEncryptor;
import com.emil.linksy_user.util.MessageMode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
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
    private final LinksyCacheManager linksyCacheManager;
    private final LinksyEncryptor encryptor;
    private final ChatService chatService;
    public void registerUser(String username, String email, String password) {
        var users = linksyCacheManager.getAllUsers();
        var emails = users.stream().map(user -> encryptor.decrypt(user.getEmail())).toList();
        if (emails.contains(email)) {
            throw new UserAlreadyExistsException("Пользователь с таким email уже существует");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setAvatarUrl("null");
        user.setOnline(false);
        user.setBlocked(false);
        user.setDeleted(false);
        user.setConfirmed(false);
        user.setMessageMode(MessageMode.ALL);
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
    @Transactional
    public void confirmCode(String email, String code) {
        User user = pendingUsers.get(email);
        if (user == null || !CodeGenerator.isValidCode(email, code)) {
            throw new InvalidVerificationCodeException("Неверный код подтверждения");
        }
        user.setEmail(encryptor.encrypt(email));
        user = userRepository.save(user);
        pendingUsers.remove(email);
        CodeGenerator.removeCode(email);
        linksyCacheManager.cacheUser(user);
        chatService.sendGreetingLetter(user);
    }

    public void requestPasswordChange(String email) {
        userRepository.findAll().stream()
                .filter(u -> encryptor.decrypt(u.getEmail()).equals(email))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с таким email не найден"));

        sendCodeToConfirmThePasswordChange(email);
    }

    public void confirmPasswordChange(String email, String code, String newPassword) {
        User user = userRepository.findAll().stream()
                .filter(u -> encryptor.decrypt(u.getEmail()).equals(email))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с таким email не найден"));
        if (!CodeGenerator.isValidCode(email, code)) {
            throw new InvalidVerificationCodeException("Неверный код подтверждения");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        CodeGenerator.removeCode(email);
        linksyCacheManager.cacheUser(user);
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public Token logIn(String email, String password) {
        return userRepository.findAll().stream()
                .filter(u -> encryptor.decrypt(u.getEmail()).equals(email))
                .findFirst()
                .filter(user -> validatePassword(password, user.getPassword()))
                .map(user -> {
                    if (user.getBlocked()) {
                        user.setRefreshToken(null);
                        user.setWsToken(null);
                        user.setOnline(false);
                        userRepository.save(user);
                        throw new BlockedException("User is blocked");}
                    String accessToken = jwtToken.generateAccessToken(user.getId().toString());
                    String refreshToken = jwtToken.generateRefreshToken(user.getId().toString());
                    String wsToken = UUID.randomUUID().toString() + UUID.randomUUID();
                    user.setWsToken(encryptor.encrypt(wsToken));
                    user.setRefreshToken(encryptor.encrypt(refreshToken));
                    user.setOnline(true);
                    userRepository.save(user);
                    linksyCacheManager.cacheUser(user);
                   return new Token(accessToken,refreshToken,wsToken);
                })
                .orElseThrow(() -> new NotFoundException("Invalid email or password"));
    }

    public Token refreshAccessToken(String refreshToken) {
        if (!jwtToken.validateRefreshToken(refreshToken)) {throw new InvalidTokenException("Invalid refresh token");}
         Long userId = jwtToken.extractUserId(refreshToken, TokenType.REFRESH);
         User user = userRepository.findById(userId)
                 .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if(user.getBlocked()) throw new BlockedException("User is blocked");

        user.setOnline(true);
        user.setLastActive(LocalDateTime.now());
         if (!encryptor.decrypt(user.getRefreshToken()).equals(refreshToken)) {throw new InvalidTokenException("Invalid refresh token");}
        String newAccessToken = jwtToken.generateAccessToken(String.valueOf(userId));
        String newRefreshToken = refreshToken;
        String newWsToken = encryptor.decrypt(user.getWsToken());
        if (jwtToken.needsRefreshRenewal(refreshToken)) {
            newRefreshToken = jwtToken.generateRefreshToken(String.valueOf(userId));
            newWsToken = UUID.randomUUID().toString() + UUID.randomUUID();
            user.setRefreshToken(encryptor.encrypt(newRefreshToken));
            user.setWsToken(encryptor.encrypt(newWsToken));
        }
        userRepository.save(user);
        linksyCacheManager.cacheUser(user);
        return new Token(newAccessToken, newRefreshToken,newWsToken);
    }

    public UserProfileData getUserProfileData(Long userId) {
        User user = linksyCacheManager.getUserById(userId);
        return new UserProfileData(userId,user.getUsername(),user.getLink(), user.getAvatarUrl(),user.getConfirmed());
    }

    public AllUserData getAllUserData(Long userId) {
        User user = linksyCacheManager.getUserById(userId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String birthday = null;
        if (user.getBirthday()!=null)
          birthday = dateFormat.format(user.getBirthday());
        return new AllUserData(user.getUsername(), user.getAvatarUrl(), encryptor.decrypt(user.getEmail()),user.getLink(),birthday);
    }


    public void saveUserAvatar (MediaResponse response) {
        Long userId = response.getId();
        String avatarUrl = response.getUrl();
        synchronized (getUserLock(userId)) {
            User user = linksyCacheManager.getUserById(userId);
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);
            linksyCacheManager.cacheUser(user);
        }
    }

    public void updateUsername(Long userId, String newUsername) {
        synchronized (getUserLock(userId)) {
            User user = linksyCacheManager.getUserById(userId);
            user.setUsername(newUsername);
            userRepository.save(user);
            linksyCacheManager.cacheUser(user);
        }
    }

    public void updateLink(Long userId, String link) {
        synchronized (getUserLock(userId)) {
            User user = linksyCacheManager.getUserById(userId);
            if (userRepository.existsByLinkAndIdNot(link, userId)) {
                throw new LinkAlreadyExistsException("Link is already in use by another user");
            }
            if(link.isEmpty()) user.setLink(null);
            else user.setLink(link);
            userRepository.save(user);
            linksyCacheManager.cacheUser(user);
        }
    }

    public void updateBirthday(Long userId, String birthday) throws ParseException {
        synchronized (getUserLock(userId)) {
            User user =  linksyCacheManager.getUserById(userId);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date newBirthday = dateFormat.parse(birthday);
            user.setBirthday(newBirthday);
            userRepository.save(user);
            linksyCacheManager.cacheUser(user);
        }
    }

    public void deleteAvatar(Long userId) {
        synchronized (getUserLock(userId)) {
            User user = linksyCacheManager.getUserById(userId);
            user.setAvatarUrl("null");
            userRepository.save(user);
            linksyCacheManager.cacheUser(user);
        }
    }
    public Token changePassword(Long userId, ChangePassword changePassword) {
        User user = linksyCacheManager.getUserById(userId);
        boolean correctPassword = validatePassword(changePassword.getOldPassword(), user.getPassword());
        if(!correctPassword) {throw new NotFoundException("Invalid password");}
            user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
            String accessToken = jwtToken.generateAccessToken(String.valueOf(userId));
            String refreshToken = jwtToken.generateRefreshToken(String.valueOf(userId));
            String wsToken = UUID.randomUUID().toString() + UUID.randomUUID();
            user.setWsToken(wsToken);
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
            linksyCacheManager.cacheUser(user);
            return new Token (accessToken,refreshToken,wsToken);
    }

    private Object getUserLock(Long userId) {
        userLocks.putIfAbsent(userId, new Object());
        return userLocks.get(userId);
    }

    public MessageMode getUserMessageMode(Long userId){
        User user = linksyCacheManager.getUserById(userId);
        return user.getMessageMode();
    }

    public void setMessageMode (Long userId,MessageMode type){
        User user = linksyCacheManager.getUserById(userId);
        user.setMessageMode(type);
        userRepository.save(user);
        linksyCacheManager.cacheUser(user);
    }




}

