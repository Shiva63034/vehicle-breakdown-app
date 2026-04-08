package com.breakdown.controller;

import com.breakdown.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.time.LocalDateTime;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setSentAt(LocalDateTime.now());
        // Send to both user and mechanic in same booking room
        messagingTemplate.convertAndSend(
            "/topic/chat/" + chatMessage.getBookingId(),
            chatMessage
        );
    }
}