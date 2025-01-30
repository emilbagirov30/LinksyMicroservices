package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.entity.BlackList;
import com.emil.linksy_user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlackListRepository extends JpaRepository<BlackList,Long> {

    Optional<BlackList> findByInitiatorAndBlocked(User initiator, User blocked);

    List<BlackList> findByInitiator (User initiator);

    Boolean existsByInitiatorAndBlocked (User initiator,User blocked);
}
