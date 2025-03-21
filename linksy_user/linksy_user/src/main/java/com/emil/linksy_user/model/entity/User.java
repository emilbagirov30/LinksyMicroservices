package com.emil.linksy_user.model.entity;

import com.emil.linksy_user.util.MessageMode;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "linksy_users")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String username;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Moment> moments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMember> chats;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChannelMember> channels;

    @NotBlank
    private String password;

    @Column(name = "avatar_url")
    private String avatarUrl;
    @Column(unique = true)
    private String link;
    @Column(name = "refresh_token")
    private String RefreshToken;
    @Column(name = "ws_token")
    private String wsToken;

    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private Date birthday;

    @Column(name = "message_mode")
    @NotNull
    @Enumerated(EnumType.STRING)
    MessageMode messageMode;
    private Boolean online;
    private Boolean confirmed;
    private Boolean blocked;
    private Boolean deleted;

    @Column(name = "last_active")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    private LocalDateTime lastActive;
}