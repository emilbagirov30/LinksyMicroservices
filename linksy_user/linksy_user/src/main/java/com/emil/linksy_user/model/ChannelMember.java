package com.emil.linksy_user.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "linksy_channel_members",  uniqueConstraints = @UniqueConstraint(columnNames = {"channel_id", "member_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChannelMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private User user;
}
