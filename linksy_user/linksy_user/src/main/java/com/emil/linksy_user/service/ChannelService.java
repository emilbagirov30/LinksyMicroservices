package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.LinkAlreadyExistsException;
import com.emil.linksy_user.exception.NotFoundException;
import com.emil.linksy_user.model.*;
import com.emil.linksy_user.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final ChannelPostRepository channelPostRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final UserRepository userRepository;

    @KafkaListener(topics = "channelResponse", groupId = "group_id_channel", containerFactory = "channelKafkaResponseKafkaListenerContainerFactory")
    public void consumeChannel(ChannelKafkaResponse response) {
            User owner = userRepository.findById(response.getOwnerId())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            Channel channel = new Channel();
            channel.setOwner(owner);
            channel.setName(response.getName());
        if (channelRepository.existsByLink(response.getLink())) {
            channel.setLink(null);
        }else {
            channel.setLink(response.getLink());
        }
            channel.setAvatarUrl(response.getAvatarUrl());
            channel.setDescription(response.getDescription());
            channel.setType(response.getType());
            channelRepository.save(channel);
            ChannelMember channelMember = new ChannelMember();
            channelMember.setChannel(channel);
            channelMember.setUser(owner);
            channelMemberRepository.save(channelMember);
    }




   public List<ChannelResponse> getChannels (Long userId){
       User user = userRepository.findById(userId)
               .orElseThrow(() -> new NotFoundException("User not found"));

       List<ChannelMember> channelMembers = channelMemberRepository.findByUser(user);
       var channels =  channelMembers.stream().map(ChannelMember::getChannel);
       return channels.map(channel -> {
         var posts = channelPostRepository.findByChannel(channel);
           Double averageRating = posts.stream()
                   .mapToLong(ChannelPost::getRating)
                   .average()
                   .orElse(-1.00);
           return  new ChannelResponse (channel.getId(),channel.getOwner().getId(),channel.getName(),
                   channel.getLink(),channel.getAvatarUrl(),Math.round(averageRating * 100.0) / 100.0,channel.getType());

       }).toList();
   }


   public ChannelPageData getChannelPageData (Long finderId,Long channelId){
       User finder = userRepository.findById(finderId)
               .orElseThrow(() -> new NotFoundException("User not found"));
       Channel channel = channelRepository.findById(channelId)
               .orElseThrow(() -> new NotFoundException("Channel not found"));
       List<ChannelMember> channelMembers = channelMemberRepository.findByChannel(channel);
       Long memberCount = (long) channelMembers.size();


       var isMemberList =channelMembers.stream().filter(channelMember -> channelMember.getUser().equals(finder)).toList();
       Boolean isMember = !isMemberList.isEmpty();
       String type = channel.getType();
       String avatarUrl = channel.getAvatarUrl();
       String name = channel.getName();
       String description = channel.getDescription();
       String link = channel.getLink();

       var posts = channelPostRepository.findByChannel(channel);
       Double averageRating = posts.stream()
               .mapToLong(ChannelPost::getRating)
               .average()
               .orElse(-1.00);
       Long ownerId = channel.getOwner().getId();
       return new ChannelPageData(channelId,ownerId,name,link,avatarUrl,description,isMember,Math.round(averageRating * 100.0) / 100.0,type,memberCount);
   }



}
