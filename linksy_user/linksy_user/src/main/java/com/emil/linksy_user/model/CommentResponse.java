package com.emil.linksy_user.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    Long commentId;
    Long authorId;
    String authorAvatarUrl;
    String authorName;
    Long parentCommentId;
    String commentText;
    String date;
}
