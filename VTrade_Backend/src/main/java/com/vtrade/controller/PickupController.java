package com.vtrade.controller;

import com.vtrade.dto.PickupRequestDto;
import com.vtrade.model.PickupRequest;
import com.vtrade.service.PickupService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pickup")
public class PickupController {

    private final PickupService pickupService;

    public PickupController(PickupService pickupService) {
        this.pickupService = pickupService;
    }

    @PostMapping
    public ResponseEntity<PickupRequest> create(@AuthenticationPrincipal Long userId,
                                                  @Valid @RequestBody PickupRequestDto req) {
        return ResponseEntity.ok(pickupService.create(userId, req));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<PickupRequest>> getMine(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(pickupService.getMine(userId));
    }

    @GetMapping("/open")
    public ResponseEntity<List<PickupRequest>> getOpen() {
        return ResponseEntity.ok(pickupService.getOpenForWorkers());
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<PickupRequest> accept(@AuthenticationPrincipal Long userId,
                                                   @PathVariable Long id) {
        return ResponseEntity.ok(pickupService.accept(userId, id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PickupRequest> updateStatus(@AuthenticationPrincipal Long userId,
                                                          @PathVariable Long id,
                                                          @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(pickupService.updateStatus(userId, id, body.get("status")));
    }
}
