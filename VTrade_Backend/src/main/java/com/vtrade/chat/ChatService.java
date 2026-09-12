package com.vtrade.chat;

import com.vtrade.chat.ChatMessageRequest;
import com.vtrade.chat.ChatMessage;
import com.vtrade.auth.User;
import com.vtrade.chat.ChatMessageRepository;
import com.vtrade.marketplace.MarketplaceListingRepository;
import com.vtrade.auth.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final MarketplaceListingRepository listingRepository;

    public ChatService(ChatMessageRepository chatMessageRepository,
                       UserRepository userRepository,
                       MarketplaceListingRepository listingRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
    }

    public List<ChatMessage> getMessages(String contextType, Long contextId) {
        return chatMessageRepository.findByContextTypeAndContextIdOrderByCreatedAtAsc(contextType, contextId);
    }

    public ChatMessage send(Long userId, ChatMessageRequest req) {
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // Prevent chatting with yourself on your own marketplace listing
        if ("marketplace".equals(req.getContextType())) {
            listingRepository.findById(req.getContextId()).ifPresent(listing -> {
                if (listing.getSellerId().equals(userId)) {
                    throw new IllegalArgumentException("You cannot send messages on your own listing.");
                }
            });
        }

        ChatMessage msg = new ChatMessage();
        msg.setContextType(req.getContextType());
        msg.setContextId(req.getContextId());
        msg.setSenderId(sender.getId());
        msg.setSenderName(sender.getFirstName());
        msg.setMessage(req.getMessage());
        msg.setMessageType(req.getMessageType() == null ? "text" : req.getMessageType());
        msg.setOfferAmount(req.getOfferAmount());

        return chatMessageRepository.save(msg);
    }
}
