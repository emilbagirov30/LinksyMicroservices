package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.Chat;
import com.emil.linksy_user.model.Message;
import com.emil.linksy_user.model.Post;
import com.emil.linksy_user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findMessagesBySender(User sender);
    List<Message> findByChat(Chat chat);
}
