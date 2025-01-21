package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChannelPostCommentsRepository extends JpaRepository<ChannelPostComment,Long> {
    @Query("SELECT COUNT(u) FROM ChannelPostComment u WHERE u.channelPost = :channelPost")
    Long countByChannelPost(@Param("channelPost") ChannelPost channelPost);
    List<ChannelPostComment> findByChannelPost(ChannelPost channelPost);
}
