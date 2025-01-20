package com.emil.linksy_user.controller;

import com.emil.linksy_user.model.ChatResponse;
import com.emil.linksy_user.model.GroupResponse;
import com.emil.linksy_user.model.UserResponse;
import com.emil.linksy_user.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }


    @GetMapping("/user_chats")
    public ResponseEntity<List<ChatResponse>> getUserChats(){
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(chatService.getUserChats(userId));
    }

    @GetMapping("/users_chat_id")
    public ResponseEntity<Long> getUsersChatId(@RequestParam("id")Long user2id){
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(chatService.getChatId(userId,user2id));
    }

    @GetMapping("/group/members/{id}")
        public ResponseEntity<List<UserResponse>> getGroupMembers(@PathVariable("id") Long chatId){
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var result = chatService.getGroupMembers(userId,chatId);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/group/data/{id}")
    public ResponseEntity<GroupResponse> getGroupData(@PathVariable("id") Long chatId){
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var result = chatService.getGroupData(userId,chatId);
        return ResponseEntity.ok(result);
    }
}
