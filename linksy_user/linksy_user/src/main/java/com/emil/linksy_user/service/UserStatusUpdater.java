package com.emil.linksy_user.service;

import com.emil.linksy_user.model.entity.User;
import com.emil.linksy_user.repository.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UserStatusUpdater {

    private final UserRepository userRepository;
    private final LinksyCacheManager linksyCacheManager;
    public UserStatusUpdater(UserRepository userRepository, LinksyCacheManager linksyCacheManager) {
        this.userRepository = userRepository;
        this.linksyCacheManager = linksyCacheManager;
    }

    @Scheduled(fixedRate = 60000)
    @Async
    public void updateUserStatus() {
        List<User> users = userRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        for (User user : users) {
            if (user.getLastActive() != null &&
                    user.getOnline() &&
                    user.getLastActive().plusMinutes(4).isBefore(now)) {
                user.setOnline(false);
                userRepository.save(user);
                linksyCacheManager.cacheUser(user);
            }
        }
    }
}