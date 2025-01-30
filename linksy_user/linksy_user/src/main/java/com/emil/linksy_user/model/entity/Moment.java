package com.emil.linksy_user.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "linksy_moments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Moment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User user;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "publication_time",nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    private Date publicationTime;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "video_url")
    private String videoUrl;
    @Column(name = "audio_url")
    private String audioUrl;
    private String text;
}