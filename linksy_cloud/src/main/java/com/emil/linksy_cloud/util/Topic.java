package com.emil.linksy_cloud.util;

import lombok.Getter;

@Getter
public enum Topic {
    AVATAR_RESPONSE("avatarResponse"),
    IMAGE_POST_RESPONSE("imageResponse"),
    AUDIO_POST_RESPONSE("audioResponse"),
    VIDEO_POST_RESPONSE("videoResponse"),
    VOICE_POST_RESPONSE("voiceResponse");
    private final String topic;

    Topic(String topic) {
        this.topic = topic;
    }

}