package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.entity.ChannelPost;
import com.emil.linksy_user.model.entity.ChannelPostEvaluations;
import com.emil.linksy_user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChannelPostEvaluationsRepository extends JpaRepository<ChannelPostEvaluations,Long> {

    @Query("SELECT AVG(e.score) FROM ChannelPostEvaluations e WHERE e.channelPost.id = :channelPostId")
    Double findAverageScoreByChannelPostId(@Param("channelPostId") Long channelPostId);
    List<ChannelPostEvaluations> findByChannelPost(ChannelPost channelPost);
    ChannelPostEvaluations findByChannelPostAndUser(ChannelPost channelPost, User user);
    @Query("SELECT e.score FROM ChannelPostEvaluations e WHERE e.channelPost.id = :channelPostId AND e.user.id = :userId")
    Integer findScoreByChannelPostIdAndUserId(@Param("channelPostId") Long channelPostId, @Param("userId") Long userId);
}
