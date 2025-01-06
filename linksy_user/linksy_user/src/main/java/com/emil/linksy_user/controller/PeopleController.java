package com.emil.linksy_user.controller;

import com.emil.linksy_user.model.PostResponse;
import com.emil.linksy_user.model.UserResponse;
import com.emil.linksy_user.service.PeopleService;
import com.emil.linksy_user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/people")
public class PeopleController {
    private final PeopleService peopleService;
    public PeopleController(PeopleService peopleService) {
        this.peopleService = peopleService;

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


}
