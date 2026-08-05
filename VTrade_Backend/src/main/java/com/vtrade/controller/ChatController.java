package com.vtrade.controller;

import com.vtrade.dto.ChatMessageRequest;
import com.vtrade.model.ChatMessage;
import com.vtrade.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public ResponseEntity<List<ChatMessage>> getMessages(@RequestParam String contextType,
                                                           @RequestParam Long contextId) {
        return ResponseEntity.ok(chatService.getMessages(contextType, contextId));
    }

    @PostMapping
    public ResponseEntity<ChatMessage> send(@AuthenticationPrincipal Long userId,
                                              @Valid @RequestBody ChatMessageRequest req) {
        return ResponseEntity.ok(chatService.send(userId, req));
    }
}
