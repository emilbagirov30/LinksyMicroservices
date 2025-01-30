package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.entity.PollOptions;
import com.emil.linksy_user.model.entity.Voter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoterRepository extends JpaRepository<Voter,Long> {
    List<Voter> findByOption (PollOptions options);
}
