package com.group2.web_tmdt.websocket.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationResponseDetail {
    private Long conversationId;

    private String conversationName;

    private Set<UserResponse> members;

    private boolean isGroup;

    private LocalDateTime createdAt;

    private List<MessageResponse> messages;

    private int currentPage;

    private int totalPages;

    private long totalElements;
}
