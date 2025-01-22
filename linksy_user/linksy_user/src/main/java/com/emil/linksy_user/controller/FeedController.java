package com.emil.linksy_user.controller;


import com.emil.linksy_user.model.ChannelPostResponse;
import com.emil.linksy_user.model.PostResponse;
import com.emil.linksy_user.service.FeedService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

}
