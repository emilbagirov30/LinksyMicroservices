package com.emil.linksy_user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Entity
@EntityListeners(MessageListener.class)
@Table(name = "linksy_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date",nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    private Date date;
    private String text;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "video_url")
    private String videoUrl;
    @Column(name = "audio_url")
    private String audioUrl;
    @Column(name = "voice_url")
    private String voiceUrl;

}
class MessageListener {

    @PostPersist
    public void onPostPersist(Post entity) {

    }

    @PostUpdate
    public void onPostUpdate(Post entity) {

    }

    @PostRemove
    public void onPostRemove(Post entity) {

    }
}