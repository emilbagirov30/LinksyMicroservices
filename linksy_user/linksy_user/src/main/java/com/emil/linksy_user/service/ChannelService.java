package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.NotFoundException;
import com.emil.linksy_user.model.*;
import com.emil.linksy_user.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;




    @KafkaListener(topics = "channelResponse", groupId = "group_id_channel", containerFactory = "channelKafkaResponseKafkaListenerContainerFactory")
    public void consumeGroup(ChannelKafkaResponse response) {

            User owner = userRepository.findById(response.getOwnerId())
                    .orElseThrow(() -> new NotFoundException("User not found"));



            Channel channel = new Channel();
            channel.setOwner(owner);
            channel.setName(response.getName());
            channel.setLink(response.getLink());
            channel.setAvatarUrl(response.getAvatarUrl());
            channel.setDescription(response.getDescription());
            channel.setType(response.getType());
            channelRepository.save(channel);
    }










}
