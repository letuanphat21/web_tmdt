package com.group2.web_tmdt.websocket.service;

import com.group2.web_tmdt.dto.ApiResponse;
import com.group2.web_tmdt.websocket.dto.ConversationResponseDTO;
import com.group2.web_tmdt.websocket.dto.ConversationResponseDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;


import java.util.Set;

public interface ConversationService {

    ResponseEntity<ApiResponse<ConversationResponseDTO>> createConversation(String emailOpponent, String emailUser);

    Set<ConversationResponseDTO> getAllConversations (String email);

    ConversationResponseDetail getConversationDetail(Long conversationId,
                                                     int page,
                                                     int size, Authentication authentication);
}
