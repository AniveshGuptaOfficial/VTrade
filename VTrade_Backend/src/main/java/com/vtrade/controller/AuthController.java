package com.vtrade.controller;

import com.vtrade.dto.*;
import com.vtrade.model.User;
import com.vtrade.repository.UserRepository;
import com.vtrade.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/login/otp/send")
    public ResponseEntity<Map<String, Object>> sendOtp(@Valid @RequestBody OtpSendRequest req) {
        String otp = authService.sendOtp(req.getPhone());
        Map<String, Object> response = new HashMap<>();
        response.put("message", "OTP sent successfully.");
        if (otp != null) {
            response.put("otp", otp); // demo mode only
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/otp/verify")
    public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody OtpVerifyRequest req) {
        return ResponseEntity.ok(authService.verifyOtp(req.getPhone(), req.getOtp()));
    }

    // ── GET /auth/me ─────────────────────────────────────────────
    // Account.html calls this on load to get {user, worker} for the logged-in user
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe(@AuthenticationPrincipal Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        Map<String, Object> workerProfile = null;
        if (user.isWorker()) {
            workerProfile = new HashMap<>();
            workerProfile.put("verification_status", user.getVerificationStatus());
            workerProfile.put("bounty_points", user.getBountyPoints());
            workerProfile.put("total_deliveries", user.getTotalDeliveries());
            workerProfile.put("rating_sum", user.getRatingSum());
            workerProfile.put("rating_count", user.getRatingCount());
            workerProfile.put("is_online", user.isWorkerOnline());
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("user", user);
        resp.put("worker", workerProfile);
        return ResponseEntity.ok(resp);
    }
}
