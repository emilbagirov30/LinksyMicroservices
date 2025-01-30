package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.entity.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollRepository extends JpaRepository<Poll,Long> {
}
