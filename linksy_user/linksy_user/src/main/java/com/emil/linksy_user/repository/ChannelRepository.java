package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    boolean existsByLink(String link);
}
