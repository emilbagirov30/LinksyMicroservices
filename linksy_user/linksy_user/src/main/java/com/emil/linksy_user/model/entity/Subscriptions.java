package com.emil.linksy_user.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "linksy_subscriptions")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Subscriptions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "subscriber_id")
    private User subscriber;

}