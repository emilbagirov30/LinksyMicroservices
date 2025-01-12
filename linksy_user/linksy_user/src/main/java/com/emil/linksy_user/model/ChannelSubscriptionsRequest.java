package com.emil.linksy_user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "linksy_channel_subscription_requests")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChannelSubscriptionsRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private User user;


}
