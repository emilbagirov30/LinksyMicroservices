package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.Channel;
import com.emil.linksy_user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    boolean existsByLink(String link);
    List<Channel> findByLinkStartingWith(String prefix);
    List<Channel> findByNameStartingWith(String prefix);
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM Channel c WHERE c.link = :link AND c.id != :id")
    boolean existsByLinkAndNotId(@Param("link") String link, @Param("id") Long id);
}
