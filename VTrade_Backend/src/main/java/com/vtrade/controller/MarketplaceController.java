package com.vtrade.controller;

import com.vtrade.dto.MarketplaceListingRequest;
import com.vtrade.model.MarketplaceListing;
import com.vtrade.service.MarketplaceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/marketplace")
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

    public MarketplaceController(MarketplaceService marketplaceService) {
        this.marketplaceService = marketplaceService;
    }

    @GetMapping
    public ResponseEntity<List<MarketplaceListing>> getAll(@RequestParam(required = false) String category) {
        return ResponseEntity.ok(marketplaceService.getAll(category));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<MarketplaceListing>> getMine(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(marketplaceService.getMine(userId));
    }

    @PostMapping
    public ResponseEntity<MarketplaceListing> create(@AuthenticationPrincipal Long userId,
                                                       @Valid @RequestBody MarketplaceListingRequest req) {
        return ResponseEntity.ok(marketplaceService.create(userId, req));
    }

    @PatchMapping("/{id}/sold")
    public ResponseEntity<MarketplaceListing> markSold(@AuthenticationPrincipal Long userId,
                                                          @PathVariable Long id) {
        return ResponseEntity.ok(marketplaceService.markSold(userId, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@AuthenticationPrincipal Long userId,
                                                         @PathVariable Long id) {
        marketplaceService.delete(userId, id);
        return ResponseEntity.ok(Map.of("message", "Listing deleted."));
    }
}
