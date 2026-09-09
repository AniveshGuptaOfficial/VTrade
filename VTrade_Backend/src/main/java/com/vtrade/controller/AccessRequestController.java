package com.vtrade.controller;

import com.vtrade.dto.AccessRequestDto;
import com.vtrade.model.AccessRequest;
import com.vtrade.service.AccessRequestService;
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