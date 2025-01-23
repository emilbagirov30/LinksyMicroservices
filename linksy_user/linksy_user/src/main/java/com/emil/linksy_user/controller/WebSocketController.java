package com.emil.linksy_user.controller;

import com.emil.linksy_user.model.Status;
import com.emil.linksy_user.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
private final MessageService messageService;

    public WebSocketController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/chat/status")
    public void handleStatus(@Payload Status status) {
           messageService.sendStatus(status);
    }
}