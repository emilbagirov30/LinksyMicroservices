package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.Post;
import com.emil.linksy_user.model.User;
import com.emil.linksy_user.model.UserPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UserPostLikeRepository extends JpaRepository<UserPostLike,Long> {

    @Query("SELECT COUNT(u) FROM UserPostLike u WHERE u.post = :post")
    Long countByPost(@Param("post") Post post);
    Boolean existsByPostAndUser(Post post, User user);
    UserPostLike findByPostAndUser (Post post, User user);
}
