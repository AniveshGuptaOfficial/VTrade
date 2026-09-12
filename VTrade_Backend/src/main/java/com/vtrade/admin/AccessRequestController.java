package com.vtrade.admin;

import com.vtrade.admin.AccessRequestDto;
import com.vtrade.admin.AccessRequest;
import com.vtrade.admin.AccessRequestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/access-requests")
public class AccessRequestController {

    private final AccessRequestService accessRequestService;

    public AccessRequestController(AccessRequestService accessRequestService) {
        this.accessRequestService = accessRequestService;
    }

    // Public — anyone can submit a request to become Delivery Staff or a Partner Store.
    // The admin reviews these directly in the access_requests table (Supabase Table
    // Editor) and, if approved, creates the real account via /api/admin/provision-user.
    @PostMapping
    public ResponseEntity<AccessRequest> submit(@Valid @RequestBody AccessRequestDto dto) {
        return ResponseEntity.ok(accessRequestService.submit(dto));
    }
}