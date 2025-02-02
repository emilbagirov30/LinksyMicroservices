package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.NotFoundException;
import com.emil.linksy_user.model.*;
import com.emil.linksy_user.model.entity.*;
import com.emil.linksy_user.repository.ChatMemberRepository;
import com.emil.linksy_user.repository.ChatRepository;
import com.emil.linksy_user.repository.DeletedMessagesRepository;
import com.emil.linksy_user.repository.MessageRepository;
import com.emil.linksy_user.util.LinksyEncryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final LinksyCacheManager linksyCacheManager;
    private final DeletedMessagesRepository deletedMessagesRepository;
    private final LinksyEncryptor encryptor;
    private final String greetingMessage = "Здравствуйте!\nСпасибо за регистрацию в нашем приложении!\nМы рады вас приветствовать.\nЕсли у вас возникнут вопросы или потребуется помощь, не стесняйтесь обращаться к нам.\nМы всегда готовы помочь!";
    private final Long supportId = 3L;
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
        User user = linksyCacheManager.getUserById(userId);
        List<ChatMember> chatMembers = chatMemberRepository.findByUser(user);
        List<Chat> chats = chatMembers.stream()
                .map(ChatMember::getChat)
                .toList();

        return chats.stream()
                .map(chat -> {
          var userMessages =  messageRepository.findByChat(chat).stream()
                  .sorted(Comparator.comparing(Message::getDate)).toList();
          var filterMessages = userMessages.stream().filter(message -> !deletedMessagesRepository.existsByMessageAndUser(message,user)).toList();
          if (filterMessages.isEmpty() && !chat.getIsGroup()) return null;
          Message lastMessage = null;
          Long senderId = null;
          String lastMessageText="";
          String dateLast="";
          Long unreadMessagesCount = null;
          Boolean confirmed = false;
          SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm");
           if(!filterMessages.isEmpty()) {
               unreadMessagesCount = filterMessages.stream()
                       .filter(message -> !message.getViewed() && !message.getSender().getId().equals(userId))
                       .count();
               lastMessage = filterMessages.get(filterMessages.size() - 1);
               if (lastMessage.getText() != null) lastMessageText = encryptor.decrypt(lastMessage.getText());
               senderId = lastMessage.getSender().getId();
               dateLast = dateFormat.format(lastMessage.getDate());
           }
               String avatarUrl;
               String displayName;
               Long companionId = null;
               Boolean isGroup = chat.getIsGroup();
               if (isGroup) {
                   avatarUrl = chat.getAvatarUrl();
                   displayName = chat.getName();
               } else {
                   List<ChatMember> cm = chatMemberRepository.findByChat(chat);
                   List<User> members = cm.stream()
                           .map(ChatMember::getUser)
                           .toList();
                   User companion = members.get(0).getId().equals(userId) ? members.get(1) : members.get(0);
                   avatarUrl = companion.getAvatarUrl();
                   displayName = companion.getUsername();
                   confirmed = companion.getConfirmed();
                   companionId = companion.getId();
               }

        return new ChatResponse(chat.getId(),companionId,senderId,isGroup,avatarUrl,displayName,confirmed,lastMessageText,dateLast,unreadMessagesCount);
        }).filter(Objects::nonNull).collect(Collectors.toList());

    }


    public void sendNewChat (Long chatId){
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundException("Chat not found"));
        var chatMembers = chatMemberRepository.findByChat(chat);
        List<User> users = chatMembers.stream()
                .map(ChatMember::getUser)
                .toList();
        var userMessages =  messageRepository.findByChat(chat).stream().toList();
        String avatarUrl;
        String displayName;
        Message lastMessage;
        String lastMessageText="";
        String dateLast="";
        Long senderId = null;
        Long unreadMessagesCount = null;
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM HH:mm");
        if (!userMessages.isEmpty()) {

            lastMessage = userMessages.get(userMessages.size()-1);
            if(lastMessage.getText()!=null) lastMessageText = encryptor.decrypt(lastMessage.getText());
            dateLast = dateFormat.format(LocalDateTime.now());
            senderId = lastMessage.getSender().getId();
        }

        if (chat.getIsGroup()){
            avatarUrl = chat.getAvatarUrl();
            displayName = chat.getName();

            var response = new ChatResponse(chat.getId(),null,senderId,true,avatarUrl,displayName,false,lastMessageText,dateLast, unreadMessagesCount);
            for (User user : users) {
                unreadMessagesCount = userMessages.stream()
                        .filter(message -> !message.getViewed() && !message.getSender().getId().equals(user.getId()))
                        .count();
                response.setUnreadMessagesCount(unreadMessagesCount);
                messagingTemplate.convertAndSendToUser(encryptor.decrypt(user.getWsToken()), "/queue/chats/", response);
            }
        }else {
            List<User> members = chatMembers.stream()
                    .map(ChatMember::getUser)
                    .toList();
            var member1 = members.get(0);
            var member2 = members.get(1);
            var unreadMessagesCount1 = userMessages.stream()
                    .filter(message -> !message.getViewed() && !message.getSender().getId().equals(member1.getId()))
                    .count();
            var unreadMessagesCount2 = userMessages.stream()
                    .filter(message -> !message.getViewed() && !message.getSender().getId().equals(member2.getId()))
                    .count();
            var response1 = new ChatResponse(chat.getId(), member2.getId(), senderId,false, member2.getAvatarUrl(),
                    member2.getUsername(),member2.getConfirmed(), lastMessageText,dateLast,unreadMessagesCount1);
            var response2 = new ChatResponse(chat.getId(), member1.getId(),senderId, false, member1.getAvatarUrl(),
                    member1.getUsername(), member1.getConfirmed(),lastMessageText,dateLast,unreadMessagesCount2);
                messagingTemplate.convertAndSendToUser(encryptor.decrypt(member1.getWsToken()), "/queue/count/", response1);
                messagingTemplate.convertAndSendToUser(encryptor.decrypt(member2.getWsToken()), "/queue/count/", response2);
                messagingTemplate.convertAndSendToUser(encryptor.decrypt(member1.getWsToken()), "/queue/chats/", response1);
                messagingTemplate.convertAndSendToUser(encryptor.decrypt(member2.getWsToken()), "/queue/chats/", response2);

        }
    }






    public Long getChatId(Long user1Id, Long user2Id) {
        User user1 = linksyCacheManager.getUserById(user1Id);
        User user2 = linksyCacheManager.getUserById(user2Id);
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
        if (!nonGroupChats.isEmpty())
            return nonGroupChats.get(0).getId();
        else{
            Chat chat = new Chat ();
            chat.setIsGroup(false);
            chatRepository.save(chat);


            ChatMember chatMember = new ChatMember();
            chatMember.setChat(chat);
            chatMember.setUser(user1);
            chatMemberRepository.save(chatMember);

            ChatMember chatMember2 = new ChatMember();
            chatMember2.setChat(chat);
            chatMember2.setUser(user2);
            chatMemberRepository.save(chatMember2);

            return chat.getId();
        }
    }



    public List<UserResponse> getGroupMembers (Long userId, Long chatId) {
        User requester = linksyCacheManager.getUserById(userId);
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
                    return new UserResponse(user.getId(), user.getAvatarUrl(), user.getUsername(), user.getLink(),user.getOnline(),user.getConfirmed());
                })
                .toList();
    }




    public List<UserResponse> getGroupSenders (Long userId, Long chatId) {
        User requester = linksyCacheManager.getUserById(userId);
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundException("Chat not found"));
        boolean isMember = chatMemberRepository.existsByChatAndUser(chat, requester);
        if (!isMember) {
            throw new AccessDeniedException("User is not a member of this chat");
        }
        var senders = messageRepository.findByChat(chat).stream().map(Message::getSender).toList();
        return senders.stream()
                .map(user -> {

                    return new UserResponse(user.getId(), user.getAvatarUrl(), user.getUsername(), user.getLink(),user.getOnline(),user.getConfirmed());
                })
                .toList();
    }

    public GroupResponse getGroupData (Long userId, Long chatId){
        User user = linksyCacheManager.getUserById(userId);
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundException("Chat not found"));
            return new GroupResponse(chat.getName(), chat.getAvatarUrl());
    }




    public void clearMessagesByChat(Long userId,Long chatId){
        User user = linksyCacheManager.getUserById(userId);
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundException("Chat not found"));
        var messages = messageRepository.findByChat(chat);
        for (Message message:messages){
            DeletedMessage deletedMessage = new DeletedMessage();
            deletedMessage.setUser(user);
            deletedMessage.setMessage(message);
            deletedMessagesRepository.save(deletedMessage);
        }
    }



    @KafkaListener(topics = "groupResponse", groupId = "group_id_group", containerFactory = "groupKafkaResponseKafkaListenerContainerFactory")
    @Transactional
     public void consumeGroup(GroupKafkaResponse response) {
        Chat chat = new Chat();
        chat.setIsGroup(true);
        chat.setName(response.getName());
        chat.setAvatarUrl(response.getAvatarUrl());
        chat = chatRepository.save(chat);
        var members = response.getParticipantIds();
        List<ChatMember> chatMembers = new ArrayList<>();
        for (Long memberId : members) {
            User user = linksyCacheManager.getUserById(memberId);

            ChatMember chatMember = new ChatMember();
            chatMember.setChat(chat);
            chatMember.setUser(user);
            chatMembers.add(chatMember);
        }
        chatMemberRepository.saveAll(chatMembers);
        sendNewChat(chat.getId());

    }

    public void addMembersToGroup(Long userId,Long groupId,List<Long> newMembersList){
        User user = linksyCacheManager.getUserById(userId);
        Chat chat = chatRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Chat not found"));
        var isMember = chatMemberRepository.existsByChatAndUser(chat,user);
        if(!isMember) throw new AccessDeniedException("Пользователь не состоит в группе");
        for (Long newMemberId:newMembersList){
            ChatMember chatMember = new ChatMember();
           User newMember = linksyCacheManager.getUserById(newMemberId);
           chatMember.setUser(newMember);
           chatMember.setChat(chat);
           chatMemberRepository.save(chatMember);
        }
    }

    public void leaveTheGroup (Long userId,Long groupId){
        User user = linksyCacheManager.getUserById(userId);
        Chat chat = chatRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Chat not found"));
        ChatMember chatMember = chatMemberRepository.findByChatAndUser(chat,user);
        chatMemberRepository.delete(chatMember);
    }



    @KafkaListener(topics = "groupEditResponse", groupId = "group_id_group_edit", containerFactory = "groupEditKafkaResponseKafkaListenerContainerFactory")
    @Transactional
    public void consumeGroupEdit(GroupEditDataKafkaResponse response) {
        User user = linksyCacheManager.getUserById(response.getUserId());
        Chat chat = chatRepository.findById(response.getGroupId())
                .orElseThrow(() -> new NotFoundException("Chat not found"));
        chat.setName(response.getName());
        chat.setAvatarUrl(response.getAvatarUrl());
        chatRepository.save(chat);

    }

    @Transactional
    public void sendGreetingLetter (User user){
        User support = linksyCacheManager.getUserById(supportId);
        Chat chat = new Chat();
        chat.setName(null);
        chat.setIsGroup(false);
        chat = chatRepository.save(chat);
        ChatMember chatMember = new ChatMember();
        chatMember.setUser(user);
        chatMember.setChat(chat);

        ChatMember chatMember2 = new ChatMember();
        chatMember2.setUser(support);
        chatMember2.setChat(chat);
        chatMemberRepository.saveAll(List.of(chatMember,chatMember2));

        Message message = new Message();
        message.setChat(chat);
        message.setSender(support);
        message.setViewed(false);
        message.setEdited(false);
        message.setText(encryptor.encrypt(greetingMessage));
        messageRepository.save(message);
    }
}



