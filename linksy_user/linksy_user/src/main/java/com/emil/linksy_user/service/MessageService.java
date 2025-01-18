package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.NotFoundException;
import com.emil.linksy_user.model.*;
import com.emil.linksy_user.repository.ChatMemberRepository;
import com.emil.linksy_user.repository.ChatRepository;
import com.emil.linksy_user.repository.MessageRepository;
import com.emil.linksy_user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final ChatMemberRepository chatMemberRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRepository chatRepository;
    @Autowired
    private EntityManager entityManager;

    @KafkaListener(topics = "messageResponse", groupId = "group_id_message", containerFactory = "messageKafkaResponseKafkaListenerContainerFactory")
    @Transactional
    public void consumeMessage(MessageKafkaResponse response) {
        Long senderId = response.getSenderId();
        Long chatId = response.getChatId();
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Message message = new Message();
        message.setSender(sender);
        message.setText(response.getText());
        message.setImageUrl(response.getImageUrl());
        message.setVideoUrl(response.getVideoUrl());
        message.setAudioUrl(response.getAudioUrl());
        message.setVoiceUrl(response.getVoiceUrl());
        Chat chat;
        if(chatId==null) {
            Long recipientId = response.getRecipientId();
            User recipient = userRepository.findById(recipientId)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            chat = chatService.findOrCreatePersonalChat(sender, recipient);
            message.setChat(chat);
            messageRepository.save(message);

        }else{
           chat = chatRepository.findById(chatId)
                    .orElseThrow(() -> new NotFoundException("Chat not found"));
            message.setChat(chat);
            messageRepository.save(message);

        }
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm");
      sendMessage(chat,new MessageResponse(message.getId(), sender.getId(),chat.getId(),
             response.getText(),response.getImageUrl(),response.getVideoUrl(),
              response.getAudioUrl(),response.getVoiceUrl(), dateFormat.format(LocalDateTime.now())));
    }

    private void sendMessage (Chat chat,MessageResponse response){
        User sender = userRepository.findById(response.getSenderId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        var members =  chatMemberRepository.findByChat(chat);
        var users =members.stream().map(ChatMember::getUser).distinct().toList();
        users.forEach( user -> {
            messagingTemplate.convertAndSendToUser(user.getAccessToken(), "/queue/messages" + "/" + chat.getId() + "/", response);
                }
        );

    }

    public List<MessageResponse> getUserMessages(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<ChatMember> chatMembers= chatMemberRepository.findByUser(user);
        List<Chat> chats = chatMembers.stream().map(ChatMember::getChat).toList();
        List<Message> messages = chats.stream()
                .flatMap(chat -> messageRepository.findByChat(chat).stream())
                .toList();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return messages.stream()
                .sorted((message1, message2) -> message2.getDate().compareTo(message1.getDate()))
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getSender().getId(),
                        message.getChat().getId(),
                        message.getText(),
                        message.getImageUrl(),
                        message.getVideoUrl(),
                        message.getAudioUrl(),
                        message.getVoiceUrl(),
                        dateFormat.format(message.getDate()
                        )
                ))
                .collect(Collectors.toList());
    }


    public List<MessageResponse> getUserMessagesByChat(Long userId,Long chatId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundException("Chat not found"));
     if (!chatMemberRepository.existsByChatAndUser(chat,user) )
         throw new SecurityException("The user is not in the chat");

        var messages =  messageRepository.findByChat(chat);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return messages.stream()
                .sorted(Comparator.comparing(Message::getDate))
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getSender().getId(),
                        message.getChat().getId(),
                        message.getText(),
                        message.getImageUrl(),
                        message.getVideoUrl(),
                        message.getAudioUrl(),
                        message.getVoiceUrl(),
                        dateFormat.format(message.getDate()
                        )
                ))
                .collect(Collectors.toList());
    }

}
