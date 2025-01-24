package com.emil.linksy_user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "linksy_chat_members",  uniqueConstraints = @UniqueConstraint(columnNames = {"chat_id", "member_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private User user;
}
