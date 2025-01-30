package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.entity.Chat;
import com.emil.linksy_user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {


    @Query("""
    SELECT c 
    FROM Chat c 
    JOIN ChatMember cm1 ON cm1.chat.id = c.id
    JOIN ChatMember cm2 ON cm2.chat.id = c.id
    WHERE cm1.user = :user1
      AND cm2.user = :user2
      AND c.isGroup = false
""")
    Optional<Chat> findChatByUsers(@Param("user1") User user1, @Param("user2") User user2);


}
