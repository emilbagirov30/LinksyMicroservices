package com.emil.linksy_user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnseenSubscriptionMomentResponse {
    private Long id;
    private String avatarUrl;
    private String username;
    private Boolean confirmed;
}
