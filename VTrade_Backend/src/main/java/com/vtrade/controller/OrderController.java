package com.vtrade.controller;

import com.vtrade.dto.OrderRequest;
import com.vtrade.model.Order;
import com.vtrade.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> placeOrder(@AuthenticationPrincipal Long userId,
                                              @Valid @RequestBody OrderRequest req) {
        return ResponseEntity.ok(orderService.placeOrder(userId, req));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<Order>> getMine(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(orderService.getMine(userId));
    }

    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> getPending() {
        List<Order> orders = orderService.getPending();
        return ResponseEntity.ok(Map.of("orders", orders));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteOrder(@AuthenticationPrincipal Long userId,
                                                              @PathVariable Long id) {
        orderService.deleteOrder(userId, id);
        return ResponseEntity.ok(Map.of("message", "Order deleted."));
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<Order> acceptOrder(@AuthenticationPrincipal Long userId,
                                               @PathVariable Long id) {
        return ResponseEntity.ok(orderService.acceptOrder(userId, id));
    }
}
