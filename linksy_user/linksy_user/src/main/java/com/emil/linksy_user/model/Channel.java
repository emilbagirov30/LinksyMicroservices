package com.emil.linksy_user.model;

import com.emil.linksy_user.util.ChannelType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "linksy_channels")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Channel implements Serializable {
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
    @Enumerated(EnumType.STRING)
    private ChannelType type;

}
