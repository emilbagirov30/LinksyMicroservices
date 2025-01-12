package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.Channel;

import com.emil.linksy_user.model.ChannelPost;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelPostRepository extends JpaRepository<ChannelPost,Long> {
    List<ChannelPost> findByChannel(Channel channel);

}
