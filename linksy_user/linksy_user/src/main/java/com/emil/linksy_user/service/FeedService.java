package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.NotFoundException;
import com.emil.linksy_user.model.*;
import com.emil.linksy_user.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final ChannelRepository channelRepository;
    private final ChannelPostRepository channelPostRepository;
    private final UserRepository userRepository;
    private final ChannelMemberRepository channelMemberRepository;
     private final ChannelService channelService;
    private final SubscriptionsRepository subscriptionsRepository;
    private final PostRepository postRepository;
      private final PostService postService;

    public List<ChannelPostResponse> getAllChannelPosts (Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        var members = channelMemberRepository.findByUser(user);
        var channels = members.stream().map(ChannelMember::getChannel).toList();
        var filterChannels = channels.stream().filter(channel -> !channel.getOwner().getId().equals(userId)).toList();
        var posts = filterChannels.stream()
                .flatMap(channel -> channelPostRepository.findByChannel(channel).stream())
                .toList();
       return posts.stream().map(post -> channelService.toChannelPostResponse(user,post)).toList();
    }



    public List<PostResponse> getAllSubscriptionsPosts (Long finderId){
        User finder = userRepository.findById(finderId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<Subscriptions> subscriptionsList = subscriptionsRepository.findAllBySubscriber(finder);
        List<User> subscriptions = subscriptionsList.stream()
                .map(Subscriptions::getUser)
                .toList();

        var posts = subscriptions.stream()
                .flatMap(subscription -> postRepository.findByUser(subscription).stream())
                .toList();
        return postService.toPostResponse(finder,posts);
    }





}
