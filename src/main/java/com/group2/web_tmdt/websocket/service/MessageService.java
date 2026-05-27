package com.group2.web_tmdt.websocket.service;

import com.group2.web_tmdt.websocket.dto.ChatMessage;
import com.group2.web_tmdt.websocket.dto.ChatMessageResponse;

public interface MessageService {

    ChatMessageResponse saveMessage(ChatMessage chatMessage);
}
