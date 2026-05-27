package com.group2.web_tmdt.websocket.service.impl;

import com.group2.web_tmdt.dao.UserRepository;
import com.group2.web_tmdt.dto.ApiResponse;
import com.group2.web_tmdt.entity.Conversation;
import com.group2.web_tmdt.entity.Message;
import com.group2.web_tmdt.entity.User;
import com.group2.web_tmdt.exception.BusinessException;
import com.group2.web_tmdt.websocket.dao.ConversationRepository;
import com.group2.web_tmdt.websocket.dao.MessageRepository;
import com.group2.web_tmdt.websocket.dto.ConversationResponseDTO;
import com.group2.web_tmdt.websocket.dto.ConversationResponseDetail;
import com.group2.web_tmdt.websocket.dto.MessageResponse;
import com.group2.web_tmdt.websocket.dto.UserResponse;
import com.group2.web_tmdt.websocket.mapper.ConversationMapper;
import com.group2.web_tmdt.websocket.mapper.MessageMapper;
import com.group2.web_tmdt.websocket.mapper.UserMapper;
import com.group2.web_tmdt.websocket.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationMapper conversationMapper;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<ApiResponse<ConversationResponseDTO>> createConversation(String emailOpponent, String emailUser) {
        User user1 = userRepository.findByEmail(emailOpponent)
                .orElseThrow(() ->
                        new BusinessException("Không tìm thấy user: " + emailOpponent));

        User user2 = userRepository.findByEmail(emailUser)
                .orElseThrow(() ->
                        new BusinessException("Không tìm thấy user: " + emailUser));

        if(user1.getMaNguoiDung() == user2.getMaNguoiDung()) {
            throw new BusinessException("Không thể tự chat với chính mình");
        }

        if(!user2.getFriends().contains(user1)) {
            user2.getFriends().add(user1);
            userRepository.save(user2);
        }

        userRepository.save(user2);

        Optional<Conversation> existingConversation =
                conversationRepository.findPrivateConversation(user1, user2);



        if(existingConversation.isPresent()) {
            return ApiResponse.ok(
                    "Đã tồn tại cuộc trò chuyện",
                    conversationMapper.toDTO( existingConversation.get())
            );
        }
        Conversation conversation = new Conversation();

        Set<User> members = conversation.getMembers();
        members.add(user1);
        members.add(user2);

        conversation.setMembers(members);
        conversation.setGroup(false);
        conversation.setCreatedAt(LocalDateTime.now());

        Conversation savedConversation =
                conversationRepository.save(conversation);
        return ApiResponse.ok(
                "Tạo cuộc trò chuyện thành công",
                conversationMapper.toDTO(savedConversation)
        );
    }

    @Override
    public Set<ConversationResponseDTO> getAllConversations(String email) {
       User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException("Không tìm thấy người dùng có emaik: "+email));

       Set<Conversation> conversations = user.getConversations();

        return conversations.stream()
                .map(conversationMapper::toDTO)
                .collect(Collectors.toSet());

    }

    @Override
    public ConversationResponseDetail getConversationDetail(Long conversationId, int page, int size, Authentication authentication) {
        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BusinessException(
                                "Không tìm thấy user"
                        ));

        Conversation conversation =
                conversationRepository.findById(conversationId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Không tìm thấy conversation"
                                ));

        boolean isMember = conversation.getMembers()
                .contains(currentUser);

        if(!isMember) {
            throw new BusinessException(
                    "Bạn không có quyền truy cập conversation này"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("sentAt").descending()
        );

        Page<Message> messagePage =
                messageRepository.findByConversationId(
                        conversationId,
                        pageable
                );

        List<MessageResponse> messages =
                messagePage.getContent()
                        .stream()
                        .map(messageMapper::toDTO)
                        .toList();

        Set<UserResponse> members =
                conversation.getMembers()
                        .stream()
                        .map(userMapper::toDTO)
                        .collect(Collectors.toSet());

        return new ConversationResponseDetail(
                conversation.getId(),
                conversation.getName(),
                members,
                conversation.isGroup(),
                conversation.getCreatedAt(),
                messages,
                messagePage.getNumber(),
                messagePage.getTotalPages(),
                messagePage.getTotalElements()
        );
    }
}
