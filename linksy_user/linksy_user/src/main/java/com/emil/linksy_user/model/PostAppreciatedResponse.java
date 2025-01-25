package com.emil.linksy_user.model;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class PostAppreciatedResponse {
    private long id;
    private String avatarUrl;
    private String username;
    private String link;
    private Boolean online;
    private Boolean confirmed;
    private Integer score;
}
