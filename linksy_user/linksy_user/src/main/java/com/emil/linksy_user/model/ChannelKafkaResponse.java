package com.emil.linksy_user.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelKafkaResponse {
    private  Long ownerId;
    private String name;
    private String link;
    private String description;
    private  String type;
    private String avatarUrl;
}
