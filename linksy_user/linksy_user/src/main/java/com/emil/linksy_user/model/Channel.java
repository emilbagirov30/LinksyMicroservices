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
    private String description;
    private String link;
    @Column(name = "avatar_url")
    private String avatarUrl;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @NotNull
    private String type;

}
