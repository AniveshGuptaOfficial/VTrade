package com.vtrade.auth;

import com.vtrade.auth.User;
import com.vtrade.auth.UserRepository;
import com.vtrade.auth.AuthService;
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
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    // ── GOOGLE SIGN-IN ────────────────────────────────────────────
    // Frontend sends the Google ID token credential from Google Identity Services,
    // plus which portal ("student" | "worker" | "store") the person signed in from.
    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@Valid @RequestBody GoogleAuthRequest req) {
        return ResponseEntity.ok(authService.googleLogin(req.getIdToken(), req.getRole()));
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