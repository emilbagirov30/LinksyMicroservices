package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.NotFoundException;
import com.emil.linksy_user.model.*;
import com.emil.linksy_user.repository.MessageRepository;
import com.emil.linksy_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;



    @KafkaListener(topics = "messageResponse", groupId = "group_id_message", containerFactory = "messageKafkaResponseKafkaListenerContainerFactory")
    public void consumeMessage(MessageKafkaResponse response) {
        Long senderId = response.getSenderId();
        Long recipientId = response.getRecipientId();
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setText(response.getText());
        message.setImageUrl(response.getImageUrl());
        message.setVideoUrl(response.getVideoUrl());
        message.setAudioUrl(response.getAudioUrl());
        message.setVoiceUrl(response.getVoiceUrl());
        messageRepository.save(message);
    }
}
