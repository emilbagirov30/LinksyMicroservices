package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.Channel;
import com.emil.linksy_user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    boolean existsByLink(String link);
    List<Channel> findByLinkStartingWith(String prefix);
    List<Channel> findByNameStartingWith(String prefix);
}
