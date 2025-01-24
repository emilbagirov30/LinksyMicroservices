package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.DeletedMessage;
import com.emil.linksy_user.model.Message;
import com.emil.linksy_user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeletedMessagesRepository extends JpaRepository<DeletedMessage,Long> {
    boolean existsByMessageAndUser(Message message, User user);
    boolean existsByMessage (Message message);
}
