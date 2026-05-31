package com.group2.web_tmdt.websocket.dto;


import com.group2.web_tmdt.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationResponseDTO {

    private Long id;

    private String name;

    private boolean isGroup;

    private LocalDateTime createdAt;

    private Set<UserResponse>  members;

}
