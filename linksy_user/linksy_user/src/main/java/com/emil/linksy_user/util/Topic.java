package com.emil.linksy_user.util;

import lombok.Getter;

@Getter
public enum Topic {
    IMAGE_POST_REQUEST("imagePostRequest"),
    AUDIO_POST_REQUEST("audioPostRequest"),
    VIDEO_POST_REQUEST("videoPostRequest"),
    VOICE_POST_REQUEST("voicePostRequest");

    private final String topic;

    Topic(String topic) {
        this.topic = topic;
    }

}
