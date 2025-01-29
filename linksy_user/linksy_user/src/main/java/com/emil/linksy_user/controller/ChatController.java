package com.emil.linksy_user.controller;

import com.emil.linksy_user.exception.*;
import com.emil.linksy_user.model.ChatResponse;
import com.emil.linksy_user.model.GroupResponse;
import com.emil.linksy_user.model.UserResponse;
import com.emil.linksy_user.service.ChatService;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/group/senders/{id}")
    public ResponseEntity<List<UserResponse>> getGroupSenders(@PathVariable("id") Long chatId){
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var result = chatService.getGroupSenders(userId,chatId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> getUserChats(@RequestParam("id")Long chatId){
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
     chatService.clearMessagesByChat(userId,chatId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/group/data/{id}")
    public ResponseEntity<GroupResponse> getGroupData(@PathVariable("id") Long chatId){
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var result = chatService.getGroupData(userId,chatId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/group/add/members")
    public ResponseEntity<Void> addMembers(@RequestParam("id")Long groupId, @RequestParam("newMembers") List<Long> membersId){
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        chatService.addMembersToGroup(userId,groupId,membersId);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/group/leave")
    public ResponseEntity<Void> leaveTheGroup(@RequestParam("id")Long groupId){
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        chatService.leaveTheGroup(userId,groupId);
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
