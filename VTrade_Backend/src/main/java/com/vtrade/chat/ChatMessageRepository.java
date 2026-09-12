package com.vtrade.chat;

import com.vtrade.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByContextTypeAndContextIdOrderByCreatedAtAsc(String contextType, Long contextId);
}
