package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.Message;
import com.emil.linksy_user.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
