package com.emil.linksy_user.service;

import com.emil.linksy_user.model.*;
import com.emil.linksy_user.repository.*;
import com.emil.linksy_user.util.ChannelType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final ChannelPostRepository channelPostRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ChannelMemberRepository channelMemberRepository;
     private final ChannelService channelService;
    private final SubscriptionsRepository subscriptionsRepository;
    private final PostRepository postRepository;
    private final BlackListRepository blackListRepository;
      private final PostService postService;
      private final PeopleService peopleService;
    private final LinksyCacheManager linksyCacheManager;
    public List<ChannelPostResponse> getAllChannelPosts (Long userId){
        User user = linksyCacheManager.getUserById(userId);
        var members = channelMemberRepository.findByUser(user);
        var channels = members.stream().map(ChannelMember::getChannel).toList();
        var filterChannels = channels.stream().filter(channel -> !channel.getOwner().getId().equals(userId)).toList();
        var posts = filterChannels.stream()
                .flatMap(channel -> channelPostRepository.findByChannel(channel).stream())
                .toList();
       return posts.stream().map(post -> channelService.toChannelPostResponse(user,post)).toList();
    }



    public List<PostResponse> getAllSubscriptionsPosts (Long finderId){
        User finder = linksyCacheManager.getUserById(finderId);
        List<Subscriptions> subscriptionsList = subscriptionsRepository.findAllBySubscriber(finder);
        List<User> subscriptions = subscriptionsList.stream()
                .map(Subscriptions::getUser)
                .toList();

        var posts = subscriptions.stream()
                .flatMap(subscription -> postRepository.findByUser(subscription).stream())
                .toList();
        return postService.toPostResponse(finder,posts);
    }



    public List<RecommendationResponse> getRecommendation(Long userId) {
        User requester = linksyCacheManager.getUserById(userId);
        var channels = channelRepository.findAll();
        var users = userRepository.findAll();
        var filterChannels = channels.stream()
                .filter(channel -> !channelMemberRepository.existsByChannelAndUser(channel, requester)
                        && channel.getType() != ChannelType.PRIVATE
                        && !channel.getBlocked()
                        && !channel.getOwner().getId().equals(userId)
                        && !channelService.isMember(channel, userId))
                .toList();
        var filterUsers = users.stream()
                .filter(user -> !user.getBlocked()
                        && !blackListRepository.existsByInitiatorAndBlocked(requester, user)
                        && !blackListRepository.existsByInitiatorAndBlocked(user, requester)
                        && !user.getId().equals(userId)
                        && !peopleService.isSubscriber(user, requester)
                        && !peopleService.isSubscription(requester, user))
                .toList();
        var sortedChannels = filterChannels.stream()
                .sorted(Comparator.comparingDouble(channelService::getAverageRating)
                        .thenComparing(channelService::getMemberCount)
                        .thenComparing(Channel::getConfirmed))
                .toList();
        var sortedUsers = filterUsers.stream()
                .sorted(Comparator.comparingLong(postService::likesCount)
                        .thenComparing(User::getConfirmed))
                .toList();
        var responseChannelList = sortedChannels.stream()
                .map(channel -> new RecommendationResponse(channel.getId(), null, channel.getAvatarUrl(), channel.getName(), channel.getLink(), channel.getConfirmed()))
                .toList();
        var responseUserList = sortedUsers.stream()
                .map(user -> new RecommendationResponse(null, user.getId(), user.getAvatarUrl(), user.getUsername(), user.getLink(), user.getConfirmed()))
                .toList();
        List<RecommendationResponse> combinedList = new ArrayList<>();
        Iterator<RecommendationResponse> channelIterator = responseChannelList.iterator();
        Iterator<RecommendationResponse> userIterator = responseUserList.iterator();
        while (channelIterator.hasNext() || userIterator.hasNext()) {
            if (channelIterator.hasNext()) {
                combinedList.add(channelIterator.next());
            }
            if (userIterator.hasNext()) {
                combinedList.add(userIterator.next());
            }
        }

        return combinedList;
    }


}
