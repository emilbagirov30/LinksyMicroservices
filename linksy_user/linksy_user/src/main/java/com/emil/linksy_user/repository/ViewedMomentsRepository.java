package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.entity.Moment;
import com.emil.linksy_user.model.entity.User;
import com.emil.linksy_user.model.entity.ViewedMoment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ViewedMomentsRepository extends JpaRepository<ViewedMoment,Long> {

     boolean existsByUserAndMoment (User user, Moment moment);
     List<ViewedMoment> findByMoment(Moment moment);
}
