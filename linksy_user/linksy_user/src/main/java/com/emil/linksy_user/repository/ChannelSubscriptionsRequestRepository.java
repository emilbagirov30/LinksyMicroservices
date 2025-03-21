package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.entity.Channel;
import com.emil.linksy_user.model.entity.ChannelSubscriptionsRequest;
import com.emil.linksy_user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelSubscriptionsRequestRepository extends JpaRepository<ChannelSubscriptionsRequest,Long> {
    List<ChannelSubscriptionsRequest> findByChannel(Channel channel);
    ChannelSubscriptionsRequest findByUserAndChannel(User user, Channel channel);
}
