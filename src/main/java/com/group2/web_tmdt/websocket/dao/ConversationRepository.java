package com.group2.web_tmdt.websocket.dao;


import com.group2.web_tmdt.entity.Conversation;
import com.group2.web_tmdt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("""
    SELECT c FROM Conversation c
    JOIN c.members m
    WHERE c.isGroup = false
    AND m IN (:user1, :user2)
    GROUP BY c
    HAVING COUNT(m) = 2
""")
    Optional<Conversation> findPrivateConversation(
            @Param("user1") User user1,
            @Param("user2") User user2
    );
}
