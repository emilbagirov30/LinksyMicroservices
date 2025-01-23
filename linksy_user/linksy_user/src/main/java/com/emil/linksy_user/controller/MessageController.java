package com.emil.linksy_user.controller;

import com.emil.linksy_user.exception.*;
import com.emil.linksy_user.model.MessageResponse;
import com.emil.linksy_user.service.MessageService;
import org.springframework.http.HttpStatus;
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


    @PutMapping("/edit")
    public ResponseEntity<Void> editMessage(@RequestParam("id") Long messageId, @RequestParam("text") String text)  {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        messageService.editMessage(userId,messageId,text);
        return ResponseEntity.ok().build();
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> handleUserNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
    }
    @ExceptionHandler(LinkAlreadyExistsException.class)
    public ResponseEntity<Void> handleLinkAlreadyExist(LinkAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
    }
    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<Void> handleInvalidVerificationCode(InvalidVerificationCodeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Void> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
    }
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Void> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
    }
}