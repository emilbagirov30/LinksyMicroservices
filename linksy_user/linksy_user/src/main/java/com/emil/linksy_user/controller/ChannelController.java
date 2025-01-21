package com.emil.linksy_user.controller;

import com.emil.linksy_user.model.*;
import com.emil.linksy_user.service.ChannelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/channels")
public class ChannelController {
private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @GetMapping("/user_channels")
    public ResponseEntity<List<ChannelResponse>> getChannels (){
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         var result = channelService.getChannels(userId);
         return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChannelPageData> getChannelData(@PathVariable("id") Long id) {
        Long finderId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var pageData = channelService.getChannelPageData(finderId,id);
        return ResponseEntity.ok(pageData);
    }


    @PostMapping("/submit")
    public ResponseEntity<Void> submitRequest(@RequestParam("id") Long channelId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        channelService.submitRequest(userId,channelId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete_request")
    public ResponseEntity<Void> deleteRequest(@RequestParam("id") Long channelId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        channelService.deleteRequest(userId,channelId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/subscriptions_request")
    public ResponseEntity<List<UserResponse>> getChannelSubscriptionRequests(@RequestParam("id") Long channelId) {
        Long useId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var subscriptions = channelService.getChannelSubscriptionRequests(useId,channelId);
        return ResponseEntity.ok(subscriptions);
    }

    @PostMapping("/requests/accept")
    public ResponseEntity<Void> acceptUserToChannel(@RequestParam("channelId") Long channelId,
                                                    @RequestParam("candidateId") Long candidateId) {
        Long ownerId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        channelService.acceptUserToChannel(ownerId,channelId,candidateId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/requests/reject")
    public ResponseEntity<Void>  rejectSubscriptionRequest(@RequestParam("channelId") Long channelId,
                                                    @RequestParam("candidateId") Long candidateId) {
        Long ownerId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        channelService.rejectSubscriptionRequest(ownerId,channelId,candidateId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<List<ChannelPostResponse>> getChannelsPost(@PathVariable("id") Long channelId) {
        Long useId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var posts = channelService.getChannelsPost(useId,channelId);
        return ResponseEntity.ok(posts);
    }


    @GetMapping("/members/{id}")
    public ResponseEntity<List<UserResponse>> getGroupMembers(@PathVariable("id") Long channelId){
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var result = channelService.getChannelMembers(userId,channelId);
        return ResponseEntity.ok(result);
    }
    @DeleteMapping("/delete_post")
    public ResponseEntity<Void> deletePost(@RequestParam Long channelId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       channelService.deletePost(userId,channelId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/subscribe/{id}")
    public ResponseEntity<Void> subscribe(@PathVariable("id") Long id) {
        Long subscriberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        channelService.subscribe(subscriberId,id);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/unsubscribe/{id}")
    public ResponseEntity<Void> unsubscribe(@PathVariable("id") Long id) {
        Long subscriberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        channelService.unsubscribe(subscriberId,id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/poll/option/vote/{id}")
    public ResponseEntity<Void> vote(@PathVariable("id") Long optionId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        channelService.vote(userId,optionId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/find/link")
    public ResponseEntity<List<ChannelResponse>> findChannelByLink(@RequestParam("prefix") String prefix) {
       var channels = channelService.findByLink(prefix);
        return ResponseEntity.ok(channels);
    }
    @GetMapping("/find/name")
    public ResponseEntity<List<ChannelResponse>> findChannelByName(@RequestParam("prefix") String prefix) {
        var channels = channelService.findByName(prefix);
        return ResponseEntity.ok(channels);
    }



    @GetMapping("/management")
    public ResponseEntity<ChannelManagementResponse> getChannelManagementData(@RequestParam("id") Long channelId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var response = channelService.getChannelManagementData(userId,channelId);
        return ResponseEntity.ok(response);
    }
}
