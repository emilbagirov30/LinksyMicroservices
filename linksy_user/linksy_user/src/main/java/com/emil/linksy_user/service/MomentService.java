package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.UserNotFoundException;
import com.emil.linksy_user.model.*;
import com.emil.linksy_user.repository.MomentRepository;
import com.emil.linksy_user.repository.PostRepository;
import com.emil.linksy_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final UserRepository userRepository;
    private final MomentRepository momentRepository;


    @KafkaListener(topics = "momentResponse", groupId = "group_id_moment", containerFactory = "momentKafkaResponseKafkaListenerContainerFactory")
    public void consumePost(MomentKafkaResponse response) {
        Long authorId = response.getAuthorId();
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Moment newMoment = new Moment();
        newMoment.setUser(author);
        newMoment.setText(response.getText());
        newMoment.setImageUrl(response.getImageUrl());
        newMoment.setVideoUrl(response.getVideoUrl());
        newMoment.setAudioUrl(response.getAudioUrl());
        momentRepository.save(newMoment);
    }
}