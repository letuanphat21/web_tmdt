package com.group2.web_tmdt.websocket.controller;


import com.group2.web_tmdt.dto.ApiResponse;
import com.group2.web_tmdt.entity.Conversation;
import com.group2.web_tmdt.websocket.dto.ConversationResponseDTO;
import com.group2.web_tmdt.websocket.dto.ConversationResponseDetail;
import com.group2.web_tmdt.websocket.dto.CreateConversationRequest;
import com.group2.web_tmdt.websocket.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;


    @PostMapping
    public ResponseEntity<ApiResponse<ConversationResponseDTO>> createConversation(@RequestBody CreateConversationRequest request, Authentication authentication){
    return   conversationService.createConversation(request.getEmailOpponent(),authentication.getName());
    }

    @GetMapping ResponseEntity<ApiResponse<Set<ConversationResponseDTO>>> getConversations(Authentication authentication){

        Set<ConversationResponseDTO> conversations = conversationService.getAllConversations(authentication.getName());

        return ApiResponse.ok("Lấy ra danh sách conversation của user hiên tại", conversations);
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<ApiResponse<ConversationResponseDetail>> getConversationById(@PathVariable Long conversationId,
                                                                                       @RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "50") int size,Authentication authentication){
        return ApiResponse.ok(
                "Lấy cuộc trò chuyện thành công",
                conversationService.getConversationDetail(
                        conversationId,
                        page,
                        size,authentication
                )
        );
    }

}
