package com.emil.linksy_user.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "linksy_chats")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Chat {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String name;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "isgroup")
    @NotNull
    private Boolean isGroup;
}
