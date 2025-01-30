package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.entity.DeletedMessage;
import com.emil.linksy_user.model.entity.Message;
import com.emil.linksy_user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeletedMessagesRepository extends JpaRepository<DeletedMessage,Long> {
    boolean existsByMessageAndUser(Message message, User user);
    boolean existsByMessage (Message message);
}
