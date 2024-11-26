package com.emil.linksy_cloud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvatarRequest {
    private Long userId;
    private byte[] fileBytes;
}
