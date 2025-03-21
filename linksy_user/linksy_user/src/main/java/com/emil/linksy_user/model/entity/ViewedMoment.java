package com.emil.linksy_user.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "linksy_viewed_moments",  uniqueConstraints = @UniqueConstraint(columnNames = {"moment_id", "user_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ViewedMoment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "moment_id")
    private Moment moment;


}
