package com.emil.linksy_user.controller;

import com.emil.linksy_user.model.MomentResponse;
import com.emil.linksy_user.model.PostResponse;
import com.emil.linksy_user.model.UserPageData;
import com.emil.linksy_user.model.UserResponse;
import com.emil.linksy_user.service.MomentService;
import com.emil.linksy_user.service.PeopleService;
import com.emil.linksy_user.service.PostService;
import com.emil.linksy_user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/people")
public class PeopleController {
    private final PeopleService peopleService;
    private final PostService postService;
    private final MomentService momentService;
    public PeopleController(PeopleService peopleService, PostService postService, MomentService momentService) {
        this.peopleService = peopleService;
        this.postService = postService;
        this.momentService = momentService;
    }
    @GetMapping("/find/link")
    public ResponseEntity<List<UserResponse>> findUsersByLink(@RequestParam("startsWith") String startsWith) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UserResponse> users = peopleService.findByLink(userId,startsWith);
        return ResponseEntity.ok(users);
    }
    @GetMapping("/find/username")
    public ResponseEntity<List<UserResponse>> findUsersByUsername(@RequestParam("startsWith") String startsWith) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UserResponse> users = peopleService.findByUsername(userId,startsWith);
        return ResponseEntity.ok(users);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserPageData> getUserData(@PathVariable("id") Long id) {
        UserPageData userPageData = peopleService.getUserPageData(id);
        return ResponseEntity.ok(userPageData);
    }
    @GetMapping("/user_posts/{id}")
    public ResponseEntity<List<PostResponse>> getUserPosts(@PathVariable("id") Long id) {
        List<PostResponse> userPosts = postService.getUserPosts(id);
        return ResponseEntity.ok(userPosts);
    }
    @GetMapping("/user_moments/{id}")
    public ResponseEntity<List<MomentResponse>> getUserMoments(@PathVariable("id") Long id) {
        List<MomentResponse> userMoments = momentService.getUserMoments(id);
        return ResponseEntity.ok(userMoments);
    }
}
