package com.emil.linksy_user.controller;


import com.emil.linksy_user.model.CommentRequest;
import com.emil.linksy_user.model.CommentResponse;
import com.emil.linksy_user.model.PostResponse;
import com.emil.linksy_user.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/like/add/{id}")
    public ResponseEntity<Void> addLike(@PathVariable ("id") Long postId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       postService.addLike(userId,postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/like/delete/{id}")
    public ResponseEntity<Void> deleteLike(@PathVariable ("id") Long postId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        postService.deleteLike(userId,postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add/comment")
    public ResponseEntity<Void> addLike(@RequestBody CommentRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        postService.addComment(userId,request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getPostComments(@PathVariable("id") Long postId) {
       var comments = postService.getAllPostComments(postId);
        return ResponseEntity.ok(comments);
    }

}
