package com.vtrade.chat;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** "marketplace" or "pickup" */
    @Column(nullable = false)
    private String contextType;

    /** ID of the marketplace listing or pickup request */
    @Column(nullable = false)
    private Long contextId;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private String senderName;

    @Column(length = 2000, nullable = false)
    private String message;

    /** text | offer | qr | system */
    private String messageType = "text";

    private Double offerAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
