package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.entity.Poll;
import com.emil.linksy_user.model.entity.PollOptions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PollOptionsRepository  extends JpaRepository<PollOptions,Long> {

    List<PollOptions> findByPoll(Poll poll);

}
