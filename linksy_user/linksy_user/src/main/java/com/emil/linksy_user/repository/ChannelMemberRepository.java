package com.emil.linksy_user.repository;


import com.emil.linksy_user.model.ChannelMember;
import com.emil.linksy_user.model.Moment;
import com.emil.linksy_user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelMemberRepository  extends JpaRepository<ChannelMember, Long> {

    List<ChannelMember> findByUser(User user);

}
