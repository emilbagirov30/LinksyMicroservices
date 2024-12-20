package com.emil.linksy_user.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostKafkaResponse {
    private Long authorId;
    private String text;
    private String imageUrl;
    private String videoUrl;
    private String audioUrl;
    private String voiceUrl;
}
