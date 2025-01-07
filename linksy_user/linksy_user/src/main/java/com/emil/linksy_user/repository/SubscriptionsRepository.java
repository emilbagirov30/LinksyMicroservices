package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.Subscriptions;
import com.emil.linksy_user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionsRepository extends JpaRepository<Subscriptions, Long> {

    long countBySubscriber(User subscriber);
    long countByUser(User user);
    boolean existsByUserAndSubscriber(User user, User subscriber);
    Optional<Subscriptions> findByUserAndSubscriber(User user, User subscriber);

    List<Subscriptions> findAllByUser(User user);
    List<Subscriptions> findAllBySubscriber(User subscriber);

}
