package com.emil.linksy_user.controller;

import com.emil.linksy_user.model.MessageResponse;
import com.emil.linksy_user.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/user_messages")
    public ResponseEntity<List<MessageResponse>> getUserMessages()  {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var messages = messageService.getUserMessages(userId);
        return ResponseEntity.ok(messages);
    }


    @GetMapping("/by/chat/{chatId}")
    public ResponseEntity<List<MessageResponse>> getUserMessagesByChat(@PathVariable("chatId")Long chatId)  {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var messages = messageService.getUserMessagesByChat(userId,chatId);
        return ResponseEntity.ok(messages);
    }
    @PutMapping("/viewed/chat/{chatId}")
    public ResponseEntity<Void> setViewed(@PathVariable("chatId")Long chatId)  {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        messageService.setViewed(userId,chatId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteMessage(@RequestParam("id") Long messageId)  {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        messageService.deleteMessage(userId,messageId);
        return ResponseEntity.ok().build();
    }
}