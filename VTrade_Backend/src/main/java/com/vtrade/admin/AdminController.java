package com.vtrade.admin;

import com.vtrade.admin.ProvisionUserRequest;
import com.vtrade.auth.User;
import com.vtrade.admin.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @Value("${vtrade.admin.secret}")
    private String adminSecret;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ── PROVISION DELIVERY STAFF / PARTNER STORE ACCOUNT ──────────────
    // Protected by a shared secret header instead of JWT auth, since the
    // person calling this isn't logged in as a platform user — they're the
    // site admin. Set a strong value for vtrade.admin.secret via the
    // VTRADE_ADMIN_SECRET env var on Render; never commit the real value.
    @PostMapping("/provision-user")
    public ResponseEntity<User> provisionUser(@RequestHeader("X-Admin-Secret") String suppliedSecret,
                                                @Valid @RequestBody ProvisionUserRequest req) {
        if (adminSecret == null || adminSecret.isBlank() || !adminSecret.equals(suppliedSecret)) {
            throw new IllegalArgumentException("Invalid admin secret.");
        }
        return ResponseEntity.ok(adminService.provisionUser(req));
    }
}