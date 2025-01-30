package com.emil.linksy_user.model;

import com.emil.linksy_user.util.MessageMode;
import lombok.*;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserPageData {
    private String username;
    private String link;
    private String avatarUrl;
    private String birthday;
    private Boolean isSubscriber;
    private Long subscriptionsCount;
    private Long subscribersCount;
    private Boolean isPageOwnerBlockedByViewer;
    private MessageMode messageMode;
    private Boolean isSubscription;
    private Boolean confirmed;
    private Boolean online;
    private String lastActive;
}
