package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.*;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {
    List<ChatMember> findByUser(User user);
    List<ChatMember> findByChat(Chat chat);
    boolean existsByChatAndUser(Chat chat, User user);

}
