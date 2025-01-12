package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.Poll;
import com.emil.linksy_user.model.PollOptions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PollOptionsRepository  extends JpaRepository<PollOptions,Long> {

    List<PollOptions> findByPoll(Poll poll);

}
