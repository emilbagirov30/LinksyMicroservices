package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {
    List<ChatMember> findByUser(User user);
    List<ChatMember> findByChat(Chat chat);


}
