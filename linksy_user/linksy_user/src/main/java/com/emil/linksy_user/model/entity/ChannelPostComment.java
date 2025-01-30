package com.emil.linksy_user.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "linksy_channel_post_comments")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChannelPostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "post_id")
    private ChannelPost channelPost;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private ChannelPostComment parent;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User user;

    private String text;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date",nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    private Date date;
}