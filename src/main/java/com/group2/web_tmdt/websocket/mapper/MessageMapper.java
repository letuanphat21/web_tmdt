package com.group2.web_tmdt.websocket.mapper;



import com.group2.web_tmdt.entity.Message;
import com.group2.web_tmdt.websocket.dto.MessageResponse;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    public MessageResponse toDTO(Message message) {

        if(message == null) {
            return null;
        }

        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getSentAt(),
                message.getSender().getMaNguoiDung()
        );
    }
}
