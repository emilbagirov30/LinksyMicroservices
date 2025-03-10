package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.entity.Channel;

import com.emil.linksy_user.model.entity.ChannelPost;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelPostRepository extends JpaRepository<ChannelPost,Long> {
    List<ChannelPost> findByChannel(Channel channel);

}
