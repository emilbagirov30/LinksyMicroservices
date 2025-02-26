package com.emil.linksy_user.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "linksy_channel_posts_evaluations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"channel_post_id", "user_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChannelPostEvaluations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "channel_post_id")
    private ChannelPost channelPost;
    private int score;
}
