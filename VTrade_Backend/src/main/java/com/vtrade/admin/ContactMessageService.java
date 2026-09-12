package com.vtrade.admin;

import com.vtrade.admin.ContactMessageDto;
import com.vtrade.admin.ContactMessage;
import com.vtrade.admin.ContactMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;

    public ContactMessageService(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
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
}