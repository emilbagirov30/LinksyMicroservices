package com.emil.linksy_cloud.util;

import lombok.Getter;

@Getter
public enum Topic {
    AVATAR_RESPONSE("avatarResponse"),
    IMAGE_POST_RESPONSE("imagePostResponse"),
    AUDIO_POST_RESPONSE("audioPostResponse"),
    VIDEO_POST_RESPONSE("videoPostResponse"),
    VOICE_POST_RESPONSE("voicePostResponse"),
    POST_RESPONSE("postResponse"),
    MESSAGE_RESPONSE("messageResponse"),
    MOMENT_RESPONSE("momentResponse"),
    CHANNEL_RESPONSE("channelResponse"),
    CHANNEL_POST_RESPONSE("channelPostResponse"),
    GROUP_RESPONSE("groupResponse");
    private final String topic;

    Topic(String topic) {
        this.topic = topic;
    }

}