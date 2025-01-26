package com.emil.linksy_user.repository;


import com.emil.linksy_user.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChannelMemberRepository  extends JpaRepository<ChannelMember, Long> {

    List<ChannelMember> findByUser(User user);
    List<ChannelMember> findByChannel(Channel channel);
    Optional<ChannelMember> findByUserAndChannel(User user, Channel channel);
    boolean existsByChannelAndUser (Channel channel,User user);
}
