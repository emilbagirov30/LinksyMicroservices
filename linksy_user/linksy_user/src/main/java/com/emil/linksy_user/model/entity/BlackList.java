package com.emil.linksy_user.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "linksy_blacklist",
        uniqueConstraints = @UniqueConstraint(columnNames = {"bocked_by", "blocked_user"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlackList {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "blocked_by")
    private User initiator;

    @ManyToOne
    @JoinColumn(name = "blocked_user")
    private User blocked;
}
