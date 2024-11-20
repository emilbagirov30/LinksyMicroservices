package com.emil.linksy_user.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "linksy_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NotNull
    private Long author_id;

    private String text;
    @NotNull
    @NotBlank
    private int rating;
    @NotNull
    @NotBlank
    private int reposts;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "publication_time", nullable = false)
    @NotNull
    private Date publication_time;
}