package com.emil.linksy_user.controller;

import com.emil.linksy_user.exception.*;
import com.emil.linksy_user.model.MomentResponse;
import com.emil.linksy_user.service.MomentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moments")
public class MomentController {
    private final MomentService momentService;

    public MomentController(MomentService momentService) {
        this.momentService = momentService;
    }

    @GetMapping("/user_moments")
    public ResponseEntity<List<MomentResponse>> getUserMoments() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<MomentResponse> userMoments = momentService.getUserMoments(userId);
        return ResponseEntity.ok(userMoments);
    }
    @DeleteMapping("/delete_moment")
    public ResponseEntity<Void> deleteMoment(@RequestParam Long momentId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        momentService.deleteMoment(userId,momentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/view")
    public ResponseEntity<Void> viewMoment(@RequestParam Long momentId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        momentService.viewMoment(userId,momentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unseen_moments")
    public ResponseEntity<List<MomentResponse>> getUnseenMoments(@RequestParam Long userId) {
        Long finderId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<MomentResponse> userMoments = momentService.getUnseenMoments(finderId,userId);
        return ResponseEntity.ok(userMoments);
    }


}