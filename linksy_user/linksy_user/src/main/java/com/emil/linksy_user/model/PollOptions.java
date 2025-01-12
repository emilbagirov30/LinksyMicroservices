package com.emil.linksy_user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "linksy_polls")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PollOptions {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @ManyToOne
    @JoinColumn(name = "poll_id")
    private Poll poll;
    private String option;
    @Column(name = "selected_count")
    private Long selectedCount;
}
