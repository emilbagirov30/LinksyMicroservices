package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.entity.Post;
import com.emil.linksy_user.model.entity.User;
import com.emil.linksy_user.model.entity.UserPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface UserPostLikeRepository extends JpaRepository<UserPostLike,Long> {

    @Query("SELECT COUNT(u) FROM UserPostLike u WHERE u.post = :post")
    Long countByPost(@Param("post") Post post);
    Boolean existsByPostAndUser(Post post, User user);
    UserPostLike findByPostAndUser (Post post, User user);
    List<UserPostLike> findByPost (Post post);
}
