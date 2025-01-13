package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.PollOptions;
import com.emil.linksy_user.model.Voter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoterRepository extends JpaRepository<Voter,Long> {
    List<Voter> findByOption (PollOptions options);
}
