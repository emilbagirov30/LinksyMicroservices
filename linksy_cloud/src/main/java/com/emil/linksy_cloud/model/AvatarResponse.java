package com.emil.linksy_cloud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvatarResponse {
   private Long userId;
   private String avatarUrl;
}
