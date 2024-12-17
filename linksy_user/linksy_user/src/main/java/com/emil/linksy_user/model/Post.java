package com.emil.linksy_user.model;


import com.fasterxml.jackson.annotation.JsonFormat;
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
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User user;
    private String text;
    private int reposts;
    private int likes;
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
    @Column(name = "voice_url")
    private String voiceUrl;
}