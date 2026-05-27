package com.group2.web_tmdt.websocket.service.impl;


import com.group2.web_tmdt.dao.UserRepository;
import com.group2.web_tmdt.entity.Conversation;
import com.group2.web_tmdt.entity.Message;
import com.group2.web_tmdt.entity.User;
import com.group2.web_tmdt.websocket.dao.ConversationRepository;
import com.group2.web_tmdt.websocket.dao.MessageRepository;
import com.group2.web_tmdt.websocket.dto.ChatMessage;
import com.group2.web_tmdt.websocket.dto.ChatMessageResponse;
import com.group2.web_tmdt.websocket.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public ChatMessageResponse saveMessage(ChatMessage chatMessage) {
        User sender = userRepository.findByEmail(chatMessage.getSenderEmail())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người gửi: " + chatMessage.getSenderEmail()));

        Conversation conversation = conversationRepository.findById(chatMessage.getConversationId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng: " + chatMessage.getConversationId()));

        // Kiểm tra sender có phải thành viên không
        boolean isMember = conversation.getMembers().stream()
                .anyMatch(m -> m.getMaNguoiDung() ==sender.getMaNguoiDung());
        if (!isMember) {
            throw new IllegalArgumentException("Sender không phải thành viên của conversation!");
        }

        Message message = new Message();
        message.setContent(chatMessage.getContent());
        message.setSender(sender);
        message.setConversation(conversation);
        message.setSentAt(LocalDateTime.now());

        Message saved = messageRepository.save(message);

        // Trả về DTO đầy đủ
        return new ChatMessageResponse(
                saved.getId(),
                saved.getContent(),
                saved.getSentAt(),
                sender.getMaNguoiDung(),
                conversation.getId()

        );
    }
}
