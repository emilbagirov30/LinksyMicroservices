package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.entity.Chat;
import com.emil.linksy_user.model.entity.Message;
import com.emil.linksy_user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findMessagesBySender(User sender);
    List<Message> findByChat(Chat chat);
}
