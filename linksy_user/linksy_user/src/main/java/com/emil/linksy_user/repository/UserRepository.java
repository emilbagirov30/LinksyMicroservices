package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByLinkAndIdNot(String link, Long id);
    List<User> findByLinkStartingWith(String prefix);
    List<User> findByUsernameStartingWith(String prefix);

}
