package com.emil.linksy_user.controller;

import com.emil.linksy_user.model.PostDto;
import com.emil.linksy_user.model.PostResponse;
import com.emil.linksy_user.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    public PostController(PostService postService) {
        this.postService = postService;
    }

@DeleteMapping("/delete_post")
public ResponseEntity<Void> deletePost(@RequestParam Long postId) {
    Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    postService.deletePost(userId,postId);
    return ResponseEntity.ok().build();
}

    @GetMapping("/user_posts")
    public ResponseEntity<List<PostResponse>> getUserPosts() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<PostResponse> userPosts = postService.getUserPosts(userId);
        return ResponseEntity.ok(userPosts);
    }

}
