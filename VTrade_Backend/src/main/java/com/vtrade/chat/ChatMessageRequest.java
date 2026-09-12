package com.vtrade.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatMessageRequest {
    @NotBlank
    private String contextType; // "marketplace" | "pickup"

    @NotNull
    private Long contextId;

    @NotBlank
    private String message;

    private String messageType = "text"; // text | offer | qr | system

    private Double offerAmount;
}
