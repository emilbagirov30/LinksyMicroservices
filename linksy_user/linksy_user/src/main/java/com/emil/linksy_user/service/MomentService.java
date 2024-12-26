package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.UserNotFoundException;
import com.emil.linksy_user.model.*;
import com.emil.linksy_user.repository.MomentRepository;
import com.emil.linksy_user.repository.PostRepository;
import com.emil.linksy_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final UserRepository userRepository;
    private final MomentRepository momentRepository;


    @KafkaListener(topics = "momentResponse", groupId = "group_id_moment", containerFactory = "momentKafkaResponseKafkaListenerContainerFactory")
    public void consumeMoment(MomentKafkaResponse response) {
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
    public List<MomentResponse> getUserMoments(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Moment> moments = momentRepository.findByUser(user);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return moments.stream()
                .sorted((moment1, moment2) -> moment2.getPublicationTime().compareTo(moment1.getPublicationTime()))
                .map(moment -> new MomentResponse(
                        moment.getId(),
                        user.getUsername(),
                        user.getAvatarUrl(),
                        moment.getImageUrl(),
                        moment.getVideoUrl(),
                        moment.getAudioUrl(),
                        moment.getText(),
                        dateFormat.format(moment.getPublicationTime())
                ))
                .collect(Collectors.toList());
    }
    public void deleteMoment (Long userId,long momentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Moment moment = momentRepository.findById(momentId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        if (!moment.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User does not own the moment");
        }
        momentRepository.delete(moment);
    }

}