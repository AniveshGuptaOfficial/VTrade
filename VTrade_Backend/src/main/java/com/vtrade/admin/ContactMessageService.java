package com.vtrade.admin;

import com.vtrade.admin.ContactMessageDto;
import com.vtrade.admin.ContactMessage;
import com.vtrade.admin.ContactMessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final EmailService emailService;

    public ContactMessageService(ContactMessageRepository contactMessageRepository, EmailService emailService) {
        this.contactMessageRepository = contactMessageRepository;
        this.emailService = emailService;
    }

    public ContactMessage submit(ContactMessageDto dto) {
        ContactMessage msg = new ContactMessage();
        msg.setName(dto.getName().trim());
        msg.setEmail(dto.getEmail().trim().toLowerCase());
        msg.setSubject(dto.getSubject());
        msg.setMessage(dto.getMessage().trim());
        msg.setStatus("new");
        return contactMessageRepository.save(msg);
    }

    public List<ContactMessage> getAll() {
        return contactMessageRepository.findAllByOrderByCreatedAtDesc();
    }

    public ContactMessage updateStatus(Long id, String status) {
        ContactMessage msg = contactMessageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message not found."));
        msg.setStatus(status);
        return contactMessageRepository.save(msg);
    }

    /**
     * Sends the admin's reply text to the person's email via Gmail SMTP, stores the
     * reply on the record, and marks the message as replied.
     */
    public ContactMessage reply(Long id, String replyText) {
        if (replyText == null || replyText.isBlank()) {
            throw new IllegalArgumentException("Reply message cannot be empty.");
        }
        ContactMessage msg = contactMessageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message not found."));

        String subject = "Re: " + (msg.getSubject() != null && !msg.getSubject().isBlank()
                ? msg.getSubject() : "Your message to VTrade");
        String body = "Hi " + msg.getName() + ",\n\n" + replyText.trim() +
                "\n\n— VTrade Support Team\nhello@vtrade.in";

        emailService.sendReply(msg.getEmail(), subject, body);

        msg.setAdminReply(replyText.trim());
        msg.setStatus("replied");
        msg.setRepliedAt(LocalDateTime.now());
        return contactMessageRepository.save(msg);
    }
}