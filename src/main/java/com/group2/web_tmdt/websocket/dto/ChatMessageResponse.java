package com.group2.web_tmdt.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private String content;
    private LocalDateTime sentAt;
    private long senderId;
    private Long conversationId;
}
