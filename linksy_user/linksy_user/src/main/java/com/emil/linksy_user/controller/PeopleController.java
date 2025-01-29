package com.emil.linksy_user.controller;

import com.emil.linksy_user.exception.NotFoundException;
import com.emil.linksy_user.exception.BlacklistException;
import com.emil.linksy_user.exception.BlockedException;
import com.emil.linksy_user.model.*;
import com.emil.linksy_user.service.MomentService;
import com.emil.linksy_user.service.PeopleService;
import com.emil.linksy_user.service.PostService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<UserPageData> getUserPageData(@PathVariable("id") Long id) {
        Long finderId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserPageData userPageData = peopleService.getUserPageData(finderId,id);
        return ResponseEntity.ok(userPageData);
    }
    @GetMapping("/user_posts/{id}")
    public ResponseEntity<List<PostResponse>> getUserPosts(@PathVariable("id") Long id) {
        Long finderId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<PostResponse> userPosts = postService.getOutsiderUserPosts(finderId,id);
        return ResponseEntity.ok(userPosts);
    }
    @GetMapping("/user_moments/{id}")
    public ResponseEntity<List<MomentResponse>> getUserMoments(@PathVariable("id") Long id) {
        List<MomentResponse> userMoments = momentService.getUserMoments(id);
        return ResponseEntity.ok(userMoments);
    }





    @PutMapping("/subscribe/{id}")
    public ResponseEntity<Void> subscribe(@PathVariable("id") Long id) {
        Long subscriberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        peopleService.subscribe(subscriberId,id);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/unsubscribe/{id}")
    public ResponseEntity<Void> unsubscribe(@PathVariable("id") Long id) {
        Long subscriberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        peopleService.unsubscribe(subscriberId,id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user_subscribers")
    public ResponseEntity<List<UserResponse>> getUserSubscribers() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UserResponse> users = peopleService.getUserSubscribers(userId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user_subscriptions")
    public ResponseEntity<List<UserResponse>> getUserSubscriptions() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UserResponse> users = peopleService.getUserSubscriptions(userId);
        return ResponseEntity.ok(users);
    }



    @GetMapping("/outsider/user_subscribers/{id}")
    public ResponseEntity<List<UserResponse>> getOutsiderUserSubscribers(@PathVariable("id") Long id) {
        List<UserResponse> users = peopleService.getUserSubscribers(id);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/outsider/user_subscriptions/{id}")
    public ResponseEntity<List<UserResponse>> getOutsiderUserSubscriptions(@PathVariable("id") Long id) {
        List<UserResponse> users = peopleService.getUserSubscriptions(id);
        return ResponseEntity.ok(users);
    }


    @PostMapping("/blacklist/add/{id}")
    public ResponseEntity<Void> addToBlackList(@PathVariable("id") Long id) {
        Long initiatorId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         peopleService.addToBlackList(initiatorId,id);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/blacklist/remove/{id}")
    public ResponseEntity<Void> removeBlackList(@PathVariable("id") Long id) {
        Long initiatorId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        peopleService.removeFromBlackList(initiatorId,id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/blacklist/all")
    public ResponseEntity<List<UserResponse>> getBlackList() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       var list = peopleService.getEveryoneOffTheBlacklist(userId);
        return ResponseEntity.ok(list);
    }


    @PostMapping("/add/report")
    public ResponseEntity<Void> addReport(@RequestBody ReportRequest request) {
        Long senderId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        peopleService.addReport(senderId,request);
        return ResponseEntity.ok().build();
    }


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
    }


    @ExceptionHandler(BlacklistException.class)
    public ResponseEntity<String> handleBlacklistException(BlacklistException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage()); // 403
    }
    @ExceptionHandler(BlockedException.class)
    public ResponseEntity<Void> handleUserBlockedException(BlockedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
    }
}
