package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.UserNotFoundException;
import com.emil.linksy_user.model.User;
import com.emil.linksy_user.model.UserResponse;
import com.emil.linksy_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PeopleService {
    private final UserRepository userRepository;


    public List<UserResponse> findByLink(Long userId, String startsWith) {
        List<User> userList = userRepository.findByLinkStartingWith(startsWith);
        return mapToUserResponse(userId, userList);
    }

    public List<UserResponse> findByUsername(Long userId, String startsWith) {
        List<User> userList = userRepository.findByUsernameStartingWith(startsWith);
        return mapToUserResponse(userId, userList);
    }

    private List<UserResponse> mapToUserResponse(Long userId, List<User> users) {
        return users.stream()
                .filter(user -> !user.getId().equals(userId))
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getAvatarUrl(),
                        user.getUsername(),
                        user.getLink()
                ))
                .collect(Collectors.toList());
    }
}
