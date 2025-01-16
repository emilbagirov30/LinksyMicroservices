package com.emil.linksy_user.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
