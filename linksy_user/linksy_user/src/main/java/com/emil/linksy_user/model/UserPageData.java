package com.emil.linksy_user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UserPageData {
    private String username;
    private String link;
    private String avatarUrl;
    private String birthday;
    private Boolean isSubscriber;
    private Long subscriptionsCount;
    private Long subscribersCount;
}
