package com.emil.linksy_user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "linksy_channel_posts")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChannelPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;
    private String text;
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

    @ManyToOne
    @JoinColumn(name = "poll_id")
    private Poll poll;
    private Boolean edited;
}
