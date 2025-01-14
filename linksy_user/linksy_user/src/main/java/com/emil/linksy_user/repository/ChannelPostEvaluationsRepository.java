package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.ChannelPostEvaluations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelPostEvaluationsRepository extends JpaRepository<ChannelPostEvaluations,Long> {

    @Query("SELECT AVG(e.score) FROM ChannelPostEvaluations e WHERE e.channelPost.id = :channelPostId")
    Double findAverageScoreByChannelPostId(@Param("channelPostId") Long channelPostId);
}
