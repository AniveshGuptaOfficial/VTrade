package com.vtrade.admin;

import com.vtrade.admin.ContactMessageDto;
import com.vtrade.admin.ContactMessage;
import com.vtrade.admin.ContactMessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactMessageController {

    private final ContactMessageService contactMessageService;

    @Value("${vtrade.admin.secret}")
    private String adminSecret;

    public ContactMessageController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    // Public — anyone can submit a message via the Contact Us page. No auth required,
    // since the person submitting isn't necessarily a logged-in VTrade user.
    @PostMapping
    public ResponseEntity<ContactMessage> submit(@Valid @RequestBody ContactMessageDto dto) {
        return ResponseEntity.ok(contactMessageService.submit(dto));
    }

    // ── ADMIN VIEWER ──────────────────────────────────────────────
    // Protected the same way as /api/admin/** — a shared secret header, since the
    // person viewing these isn't logged in as a platform user, they're the site admin.
    @GetMapping
    public ResponseEntity<List<ContactMessage>> getAll(@RequestHeader("X-Admin-Secret") String suppliedSecret) {
        requireAdmin(suppliedSecret);
        return ResponseEntity.ok(contactMessageService.getAll());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ContactMessage> updateStatus(@RequestHeader("X-Admin-Secret") String suppliedSecret,
                                                          @PathVariable Long id,
                                                          @RequestBody Map<String, String> body) {
        requireAdmin(suppliedSecret);
        return ResponseEntity.ok(contactMessageService.updateStatus(id, body.get("status")));
    }

    private void requireAdmin(String suppliedSecret) {
        if (adminSecret == null || adminSecret.isBlank() || !adminSecret.equals(suppliedSecret)) {
            throw new IllegalArgumentException("Invalid admin secret.");
        }
    }
}