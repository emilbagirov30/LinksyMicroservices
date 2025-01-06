package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.Subscriptions;
import com.emil.linksy_user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionsRepository extends JpaRepository<Subscriptions, Long> {

    long countBySubscriber(User subscriber);

    long countByUser(User user);
}
