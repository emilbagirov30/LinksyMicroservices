package com.emil.linksy_cloud.controller;

import com.emil.linksy_cloud.service.MediaService;
import com.emil.linksy_cloud.util.LinksyTools;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chats")
public class ChatController {
    private final MediaService mediaService;

    public ChatController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/create/group")
    public ResponseEntity<Void> createGroup( @RequestParam("ids")String participantIds,
                                             @RequestParam("name") String name,
                                             @RequestParam(value = "image", required = false) MultipartFile image){
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Long> participants = Arrays.stream(LinksyTools.clearQuotes(participantIds).split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        participants.add(userId);
        mediaService.produceGroup(participants, image, name);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/edit/group")
    public ResponseEntity<Void> editGroup(   @RequestParam("id")Long groupId,
                                             @RequestParam(value = "name",required = false) String name,
                                             @RequestParam(value ="oldAvatarUrl",required = false) String oldAvatarUrl,
                                             @RequestParam(value = "image", required = false) MultipartFile image){
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        mediaService.produceEditGroup(userId,groupId,oldAvatarUrl, image, name);
        return ResponseEntity.ok().build();
    }

}
