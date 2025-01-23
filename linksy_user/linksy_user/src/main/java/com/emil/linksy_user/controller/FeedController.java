package com.emil.linksy_user.controller;


import com.emil.linksy_user.exception.*;
import com.emil.linksy_user.model.ChannelPostResponse;
import com.emil.linksy_user.model.PostResponse;
import com.emil.linksy_user.service.FeedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
public class FeedController {
private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/channels")
    public ResponseEntity<List<ChannelPostResponse>> getAllChannelsPost() {
        Long useId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var posts = feedService.getAllChannelPosts(useId);
        return ResponseEntity.ok(posts);
    }


    @GetMapping("/users")
    public ResponseEntity<List<PostResponse>> getUserPosts() {
        Long useId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var posts = feedService.getAllSubscriptionsPosts(useId);
        return ResponseEntity.ok(posts);
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
