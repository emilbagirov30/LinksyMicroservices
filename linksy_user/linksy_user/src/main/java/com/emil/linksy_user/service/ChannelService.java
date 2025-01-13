package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.NotFoundException;
import com.emil.linksy_user.model.*;
import com.emil.linksy_user.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final ChannelPostRepository channelPostRepository;
    private final PollRepository pollRepository;
    private final PollOptionsRepository pollOptionsRepository;
    private final ChannelSubscriptionsRequestRepository channelSubscriptionsRequestRepository;
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


   public void submitRequest (Long userId,Long channelId){
       User candidate = userRepository.findById(userId)
               .orElseThrow(() -> new NotFoundException("User not found"));
       Channel channel = channelRepository.findById(channelId)
               .orElseThrow(() -> new NotFoundException("Channel not found"));
       var request = channelSubscriptionsRequestRepository.findByUserAndChannel(candidate,channel);
       channelSubscriptionsRequestRepository.delete(request);
   }

    public void deleteRequest (Long userId,Long channelId){
        User candidate = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found"));
        ChannelSubscriptionsRequest request = new ChannelSubscriptionsRequest();
        request.setChannel(channel);
        request.setUser(candidate);
        channelSubscriptionsRequestRepository.save(request);
    }

   public List<UserResponse> getChannelSubscriptionRequests (Long userId,Long channelId){
       User owner = userRepository.findById(userId)
               .orElseThrow(() -> new NotFoundException("User not found"));
       Channel channel = channelRepository.findById(channelId)
               .orElseThrow(() -> new NotFoundException("Channel not found"));
       if (!channel.getOwner().equals(owner)) throw new SecurityException("User is not the owner channel");
       var candidates = channelSubscriptionsRequestRepository.findByChannel(channel);

       return candidates.stream().map(candidate ->{
           User user = candidate.getUser();
           return new UserResponse(user.getId(), user.getAvatarUrl(), user.getUsername(), user.getLink());
       }).toList();
   }

   public void acceptUserToChannel(Long ownerId,Long channelId,Long candidateId) {
       User owner = userRepository.findById(ownerId)
               .orElseThrow(() -> new NotFoundException("User not found"));
       Channel channel = channelRepository.findById(channelId)
               .orElseThrow(() -> new NotFoundException("Channel not found"));
       if (!channel.getOwner().equals(owner)) throw new SecurityException("User is not the owner channel");
       User candidate = userRepository.findById(candidateId)
               .orElseThrow(() -> new NotFoundException("User not found"));
       ChannelMember channelMember = new ChannelMember();
       channelMember.setUser(candidate);
       channelMember.setChannel(channel);
       channelMemberRepository.save(channelMember);
   }

    public void rejectSubscriptionRequest(Long ownerId,Long channelId,Long candidateId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found"));
        if (!channel.getOwner().equals(owner)) throw new SecurityException("User is not the owner channel");
        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new NotFoundException("User not found"));
      var request = channelSubscriptionsRequestRepository.findByUserAndChannel(candidate,channel);
      channelSubscriptionsRequestRepository.delete(request);
    }

    @KafkaListener(topics = "channelPostResponse", groupId = "group_id_channel", containerFactory = "channelPostKafkaResponseKafkaListenerContainerFactory")
    public void consumeChannelPost(ChannelPostKafkaResponse response) {
        User owner = userRepository.findById(response.getOwnerId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        Channel channel = channelRepository.findById(response.getChannelId())
                .orElseThrow(() -> new NotFoundException("Channel not found"));
        if (!channel.getOwner().equals(owner)) throw new SecurityException("User is not the owner channel");

        ChannelPost channelPost = new ChannelPost();
        channelPost.setChannel(channel);
        channelPost.setText(response.getText());
        channelPost.setImageUrl(response.getImageUrl());
        channelPost.setVideoUrl(response.getVideoUrl());
        if (!response.getOptions().isEmpty()) {
            Poll poll = new Poll();
            poll.setTitle(response.getPollTitle());
            pollRepository.save(poll);
            channelPost.setPoll(poll);
            var options = response.getOptions();
            for (String option:options){
                PollOptions pollOptions = new PollOptions();
                pollOptions.setPoll(poll);
                pollOptions.setOption(option);
                pollOptions.setSelectedCount(0L);
                pollOptionsRepository.save(pollOptions);
            }
        }
        channelPostRepository.save(channelPost);
    }


    public List<ChannelPostResponse> getChannelsPost (Long userId,Long channelId){
        User finder = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found"));
        List<ChannelMember> channelMembers = channelMemberRepository.findByChannel(channel);
        boolean isMember = channelMembers.stream().anyMatch(channelMember -> channelMember.getUser().equals(finder));
        if (!isMember) throw new SecurityException("User is not a member of the channels");
        var posts = channelPostRepository.findByChannel(channel);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Double averageRating = posts.stream()
                .mapToLong(ChannelPost::getRating)
                .average()
                .orElse(-1.00);
         return posts.stream().map(post ->{
                  List<OptionResponse> optionResponseList= null;
                  String title =null;
             if (post.getPoll()!=null){
                 Poll poll = pollRepository.findById(post.getPoll().getId())
                         .orElseThrow(() -> new NotFoundException("Poll not found"));
                 title = poll.getTitle();
                   var options = pollOptionsRepository.findByPoll(poll);
                   optionResponseList = options.stream().map(op ->{
                       return new OptionResponse(op.getId(), op.getOption(), op.getSelectedCount());
                   }).toList();
             }

             return new ChannelPostResponse(post.getId(), channel.getName(),
                     channel.getAvatarUrl(), post.getText(), post.getImageUrl(),
                     post.getVideoUrl(), post.getAudioUrl(), dateFormat.format(post.getPublicationTime()),title,optionResponseList,
                     Math.round(averageRating * 100.0) / 100.0,post.getReposts());
         }).toList();
    }



    public List<UserResponse> getChannelMembers (Long userId, Long channelId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
       Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Chat not found"));
        List<ChannelMember> members = channelMemberRepository.findByChannel(channel);
        boolean isMember = members.stream().anyMatch(channelMember -> channelMember.getUser().equals(requester));
        if (!isMember) throw new AccessDeniedException("User is not a member of this chat");

        return members.stream()
                .map(member -> {
                    User user = member.getUser();
                    return new UserResponse(user.getId(), user.getAvatarUrl(), user.getUsername(), user.getLink());
                })
                .toList();
    }
    public void deletePost (Long userId,long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        ChannelPost post = channelPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        if (!post.getChannel().getOwner().equals(user)) {
            throw new SecurityException("User does not own the post");
        }
       channelPostRepository.delete(post);
    }




    public void subscribe(Long subscriberId,Long channelId){
        User subscriber = userRepository.findById(subscriberId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Chat not found"));
       ChannelMember channelMember = new ChannelMember();
      channelMember.setUser(subscriber);
      channelMember.setChannel(channel);
      channelMemberRepository.save(channelMember);
    }

    public void unsubscribe(Long subscriberId,Long channelId){
        User subscriber = userRepository.findById(subscriberId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Chat not found"));
       ChannelMember channelMember = channelMemberRepository.findByUserAndChannel(subscriber,channel)
                .orElseThrow(() -> new NotFoundException(" ChannelMember not found"));
       channelMemberRepository.delete(channelMember);
    }

}
