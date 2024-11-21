package com.emil.linksy_user.controller;

import com.emil.linksy_user.model.ChangePassword;
import com.emil.linksy_user.model.PostDto;
import com.emil.linksy_user.service.PostService;
import com.emil.linksy_user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> changePassword(@RequestBody PostDto post) {
        Long authorId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        postService.createPost(authorId,post);
        return ResponseEntity.ok().build();
    }





}
