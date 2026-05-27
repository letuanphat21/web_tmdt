package com.group2.web_tmdt.websocket.dao;

import com.group2.web_tmdt.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByConversationId(
            Long conversationId,
            Pageable pageable
    );
}
