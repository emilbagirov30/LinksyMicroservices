package com.emil.linksy_user.util;

import lombok.Getter;

@Getter
public enum Topic {
    IMAGE_POST_REQUEST("imageRequest"),
    AUDIO_POST_REQUEST("audioRequest"),
    VIDEO_POST_REQUEST("videoRequest"),
    VOICE_POST_REQUEST("voiceRequest");

    private final String topic;

    Topic(String topic) {
        this.topic = topic;
    }

}
