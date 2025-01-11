package com.emil.linksy_user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "linksy_channels")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Channel {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @NotNull
    private String type;

}
