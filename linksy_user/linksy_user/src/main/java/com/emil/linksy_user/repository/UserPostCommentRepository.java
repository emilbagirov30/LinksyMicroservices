package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.Post;
import com.emil.linksy_user.model.UserPostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserPostCommentRepository extends JpaRepository<UserPostComment,Long> {

    @Query("SELECT COUNT(u) FROM UserPostComment u WHERE u.post = :post")
    Long countByPost(@Param("post") Post post);
    List<UserPostComment> findByPost(Post post);
    void deleteByPostAndParentIdIsNotNull(Post post);
    void deleteByPostAndParentIdIsNull(Post post);
}
