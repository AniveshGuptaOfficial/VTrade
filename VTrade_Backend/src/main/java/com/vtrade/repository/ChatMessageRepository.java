package com.vtrade.repository;

import com.vtrade.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByContextTypeAndContextIdOrderByCreatedAtAsc(String contextType, Long contextId);
}
