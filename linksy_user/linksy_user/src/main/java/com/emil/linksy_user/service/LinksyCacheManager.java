package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.NotFoundException;
import com.emil.linksy_user.model.entity.Channel;
import com.emil.linksy_user.model.entity.User;
import com.emil.linksy_user.repository.ChannelRepository;
import com.emil.linksy_user.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LinksyCacheManager {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private static final String USER_CACHE = "users";
    private static final String CHANNEL_CACHE = "channels";
    public LinksyCacheManager(UserRepository userRepository, ChannelRepository channelRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }


    @Cacheable(value = USER_CACHE, key = "#id")
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }


    @Cacheable(value = CHANNEL_CACHE, key = "#id")
    public Channel getChannelById(Long id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Channel not found"));
    }


    @CachePut(value = CHANNEL_CACHE, key = "#channel.id")
    public Channel cacheChannel(Channel channel) {
        return channel;
    }


    @CachePut(value = USER_CACHE, key = "#user.id")
    public User cacheUser(User user) {
        return user;
    }

    @Cacheable(value = USER_CACHE)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Cacheable(value = CHANNEL_CACHE)
    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }



    @CacheEvict(value = USER_CACHE, key = "#id")
    public void evictUser(Long id) {
    }
}