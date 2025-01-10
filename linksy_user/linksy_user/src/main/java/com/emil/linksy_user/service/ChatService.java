package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.NotFoundException;
import com.emil.linksy_user.model.*;
import com.emil.linksy_user.repository.ChatMemberRepository;
import com.emil.linksy_user.repository.ChatRepository;
import com.emil.linksy_user.repository.MessageRepository;
import com.emil.linksy_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
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
          Message lastMessage = null;
          String lastMessageText;
          String dateLast;
          SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm");
          if (!userMessages.isEmpty()) {
              lastMessage = userMessages.get(userMessages.size()-1);
              lastMessageText = lastMessage.getText();
              dateLast = dateFormat.format (lastMessage.getDate());
          }else{
              lastMessageText = "";
              dateLast = "new";
          }


          String avatarUrl;
          String displayName;
          Long companionId=null;
          Boolean isGroup = chat.getIsGroup();
          if (isGroup){
              avatarUrl = chat.getAvatarUrl();
              displayName = chat.getName();
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



    public List<UserResponse> getGroupMembers (Long userId, Long chatId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundException("Chat not found"));

        boolean isMember = chatMemberRepository.existsByChatAndUser(chat, requester);
        if (!isMember) {
            throw new AccessDeniedException("User is not a member of this chat");
        }
        List<ChatMember> members = chatMemberRepository.findByChat(chat);

        return members.stream()
                .map(member -> {
                    User user = member.getUser();
                    return new UserResponse(user.getId(), user.getAvatarUrl(), user.getUsername(), user.getLink());
                })
                .toList();
    }














    @KafkaListener(topics = "groupResponse", groupId = "group_id_group", containerFactory = "groupKafkaResponseKafkaListenerContainerFactory")
     public void consumeGroup(GroupKafkaResponse response) {
        Chat chat = new Chat();
        chat.setIsGroup(true);
        chat.setName(response.getName());
        chat.setAvatarUrl(response.getAvatarUrl());
        chat = chatRepository.save(chat);

        var members = response.getParticipantIds();

        List<ChatMember> chatMembers = new ArrayList<>();

        for (Long memberId : members) {
            User user = userRepository.findById(memberId)
                    .orElseThrow(() -> new NotFoundException("User not found"));

            ChatMember chatMember = new ChatMember();
            chatMember.setChat(chat);
            chatMember.setUser(user);
            chatMembers.add(chatMember);
        }


        chatMemberRepository.saveAll(chatMembers);

    }

}



