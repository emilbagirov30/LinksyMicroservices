package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.NotFoundException;
import com.emil.linksy_user.model.*;
import com.emil.linksy_user.repository.ChatMemberRepository;
import com.emil.linksy_user.repository.ChatRepository;
import com.emil.linksy_user.repository.MessageRepository;
import com.emil.linksy_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    public Chat findOrCreatePersonalChat(User user1, User user2) {
        return chatRepository.findChatByUsers(user1, user2)
                .orElseGet(() -> createNewChat(user1, user2));
    }

    private Chat createNewChat(User user1, User user2) {
        Chat chat = new Chat();
        chat.setIsGroup(false);
        chat.setName(null);
        chat = chatRepository.save(chat);

        ChatMember member1 = new ChatMember();
        member1.setChat(chat);
        member1.setUser(user1);

        ChatMember member2 = new ChatMember();
        member2.setChat(chat);
        member2.setUser(user2);
        chatMemberRepository.saveAll(List.of(member1, member2));
        return chat;
    }

    public List<ChatResponse> getUserChats (Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        List<ChatMember> chatMembers = chatMemberRepository.findByUser(user);
        List<Chat> chats = chatMembers.stream()
                .map(ChatMember::getChat)
                .toList();

        return chats.stream()
                .map(chat -> {
          var userMessages =  messageRepository.findByChat(chat).stream()
                  .sorted(Comparator.comparing(Message::getDate)).toList();
          Message lastMessage = userMessages.get(userMessages.size()-1);
          String avatarUrl;
          String displayName;
          Long companionId=null;
          Boolean isGroup = chat.getIsGroup();
          if (isGroup){
              avatarUrl = chat.getAvatarUrl();
              displayName = chat.getAvatarUrl();
          }else {
              List<ChatMember> cm = chatMemberRepository.findByChat(chat);
              List<User> members = cm.stream()
                      .map(ChatMember::getUser)
                      .toList();
              User companion = members.get(0).equals(user) ? members.get(1) : members.get(0);
              avatarUrl = companion.getAvatarUrl();
              displayName = companion.getUsername();
              companionId = companion.getId();
          }

          String lastMessageText = lastMessage.getText();
         SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
          String dateLast = dateFormat.format (lastMessage.getDate());
        return new ChatResponse(chat.getId(),companionId,isGroup,avatarUrl,displayName,lastMessageText,dateLast);
        }).collect(Collectors.toList());

    }

    public Long getChatId(Long user1Id, Long user2Id) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<ChatMember> user1Chats = chatMemberRepository.findByUser(user1);
        List<ChatMember> user2Chats = chatMemberRepository.findByUser(user2);

        var common = user1Chats.stream()
                .filter(member1 -> user2Chats.stream()
                        .anyMatch(member2 -> member1.getChat().equals(member2.getChat())))
                .toList();

        List<Chat> chats = common.stream().map(ChatMember::getChat).toList();
        List<Chat> nonGroupChats = chats.stream()
                .filter(chat -> !chat.getIsGroup())
                .toList();
        if (!nonGroupChats.isEmpty()) {
            var id = nonGroupChats.get(0).getId();
            return id;
        }else return -100L;
    }

}



