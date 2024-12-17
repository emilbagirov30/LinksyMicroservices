package com.emil.linksy_cloud.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${image.upload.image-dir}")
    private String uploadImageDir;
    @Value("${image.upload.video-dir}")
    private String uploadVideoDir;
    @Value("${image.upload.audio-dir}")
    private String uploadAudioDir;
    @Value("${image.upload.voice-dir}")
    private String uploadVoiceDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/" + uploadImageDir + "**")
                .addResourceLocations("file:" + uploadImageDir + "/");

        registry.addResourceHandler("/" + uploadVideoDir + "**")
                .addResourceLocations("file:" + uploadVideoDir + "/");

        registry.addResourceHandler("/" + uploadAudioDir + "**")
                .addResourceLocations("file:" + uploadAudioDir + "/");

        registry.addResourceHandler("/" + uploadVoiceDir + "**")
                .addResourceLocations("file:" + uploadVoiceDir + "/");
    }
}
