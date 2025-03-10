package com.emil.linksy_cloud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupKafkaResponse {
    private  List<Long> participantIds;
    private String avatarUrl;
    private String name;
}
