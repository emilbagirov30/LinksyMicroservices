package com.emil.linksy_cloud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupEditDataKafkaResponse {
    private Long userId;
    private Long groupId;
    private String name;
    private String avatarUrl;
}
