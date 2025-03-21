package com.emil.linksy_user.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "linksy_deleted_messages", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "message_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DeletedMessage {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;
}

