package com.group2.web_tmdt.websocket.controller;


import com.group2.web_tmdt.websocket.dto.ChatMessage;
import com.group2.web_tmdt.websocket.dto.ChatMessageResponse;
import com.group2.web_tmdt.websocket.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        // Gán đúng username của người đang kết nối (tránh giả mạo)
        chatMessage.setSenderEmail(principal.getName());

        // Lưu vào DB
        ChatMessageResponse saved = messageService.saveMessage(chatMessage);

        // Broadcast tới tất cả thành viên trong conversation
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + saved.getConversationId(),
                saved
        );
    }
}
