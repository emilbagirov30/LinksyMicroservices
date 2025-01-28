package com.emil.linksy_user.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
   private Long commentId;
    private Long authorId;
    private String authorAvatarUrl;
    private String authorName;
    private Boolean confirmed;
    private  Long parentCommentId;
    private  String commentText;
    private String date;
}
