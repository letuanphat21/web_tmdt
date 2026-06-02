package com.group2.web_tmdt.websocket.mapper;


import com.group2.web_tmdt.entity.Conversation;
import com.group2.web_tmdt.websocket.dto.ConversationResponseDTO;
import com.group2.web_tmdt.websocket.dto.MessageResponse;
import com.group2.web_tmdt.websocket.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConversationMapper {

    private final UserMapper userMapper;



    public ConversationResponseDTO toDTO(Conversation conversation) {
        if(conversation == null) return null;

        Set<UserResponse> members = conversation.getMembers().stream().map(userMapper::toDTO).collect(Collectors.toSet());


        return new ConversationResponseDTO(
                conversation.getId(),
                conversation.getName(),
                conversation.isGroup(),
                conversation.getCreatedAt(),
                members
        );

    }
}
