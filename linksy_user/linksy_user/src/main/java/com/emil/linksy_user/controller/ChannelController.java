package com.emil.linksy_user.controller;

import com.emil.linksy_user.model.ChannelPageData;
import com.emil.linksy_user.model.ChannelResponse;
import com.emil.linksy_user.model.UserPageData;
import com.emil.linksy_user.service.ChannelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
