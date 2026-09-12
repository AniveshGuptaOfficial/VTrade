package com.vtrade.profile;

import com.vtrade.profile.BecomeWorkerRequest;
import com.vtrade.profile.PasswordUpdateRequest;
import com.vtrade.profile.ProfileUpdateRequest;
import com.vtrade.profile.WorkerSlotRequest;
import com.vtrade.order.Order;
import com.vtrade.auth.User;
import com.vtrade.profile.WorkerSlot;
import com.vtrade.order.OrderService;
import com.vtrade.profile.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final OrderService orderService;

    @Value("${vtrade.uploads.dir}")
    private String uploadsDir;

    public UserController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    // ── SET / CHANGE PASSWORD ─────────────────────────────────
    // Frontend calls PUT /users/password with {password}
    @PutMapping("/password")
    public ResponseEntity<Map<String, Object>> setPassword(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PasswordUpdateRequest req) {
        User user = userService.setPassword(userId, req.getPassword());
        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Password updated successfully.");
        resp.put("has_password", user.isHasPassword());
        return ResponseEntity.ok(resp);
    }

    // ── GET PROFILE ─────────────────────────────────────────────
    // Frontend calls GET /users/profile
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    // Legacy alias used by Account.html loadProfile via /auth/me (handled in AuthController)
    @GetMapping("/me")
    public ResponseEntity<User> getProfileAlias(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    // ── UPDATE PROFILE ───────────────────────────────────────────
    // Frontend calls PUT /users/profile with {first_name, last_name, student_id, hostel_block, room_number}
    // JacksonConfig snake_case means: firstName→first_name in response,
    // but incoming JSON also uses snake_case so we accept both via @JsonProperty on the DTO
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @AuthenticationPrincipal Long userId,
            @RequestBody ProfileUpdateRequest req) {
        User user = userService.updateProfile(userId, req);
        Map<String, Object> resp = new HashMap<>();
        resp.put("user", user);
        return ResponseEntity.ok(resp);
    }

    // ── BECOME WORKER ────────────────────────────────────────────
    // Frontend calls POST /users/become-worker with {worker_type, student_id, bio}
    @PostMapping("/become-worker")
    public ResponseEntity<Map<String, Object>> becomeWorker(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody BecomeWorkerRequest req) {
        User user = userService.becomeWorker(userId, req);

        // Build a worker-profile summary for the response
        Map<String, Object> workerProfile = new HashMap<>();
        workerProfile.put("verification_status", user.getVerificationStatus());
        workerProfile.put("bounty_points", user.getBountyPoints());
        workerProfile.put("total_deliveries", user.getTotalDeliveries());
        workerProfile.put("rating_sum", user.getRatingSum());
        workerProfile.put("rating_count", user.getRatingCount());
        workerProfile.put("is_online", user.isWorkerOnline());

        Map<String, Object> resp = new HashMap<>();
        resp.put("user", user);
        resp.put("worker", workerProfile);
        return ResponseEntity.ok(resp);
    }

    // ── WORKER PROFILE (GET) ──────────────────────────────────────
    // Frontend calls GET /users/worker-profile → expects {slots, verification_status, bounty_points, ...}
    @GetMapping("/worker-profile")
    public ResponseEntity<Map<String, Object>> getWorkerProfile(
            @AuthenticationPrincipal Long userId) {
        User user = userService.getProfile(userId);
        List<WorkerSlot> slots = userService.getSlots(userId);

        Map<String, Object> resp = new HashMap<>();
        resp.put("slots", slots);
        resp.put("verification_status", user.getVerificationStatus());
        resp.put("bounty_points", user.getBountyPoints());
        resp.put("total_deliveries", user.getTotalDeliveries());
        resp.put("rating_sum", user.getRatingSum());
        resp.put("rating_count", user.getRatingCount());
        resp.put("is_online", user.isWorkerOnline());
        return ResponseEntity.ok(resp);
    }

    // ── WORKER PROFILE (PUT) ──────────────────────────────────────
    // Frontend calls PUT /users/worker-profile with {is_online: true/false}
    @PutMapping("/worker-profile")
    public ResponseEntity<Map<String, Object>> updateWorkerProfile(
            @AuthenticationPrincipal Long userId,
            @RequestBody Map<String, Object> body) {
        boolean online = Boolean.TRUE.equals(body.get("is_online"));
        User user = userService.toggleOnline(userId, online);
        Map<String, Object> resp = new HashMap<>();
        resp.put("is_online", user.isWorkerOnline());
        resp.put("message", online ? "You are now online." : "You are now offline.");
        return ResponseEntity.ok(resp);
    }

    // ── AVAILABILITY SLOTS ────────────────────────────────────────
    // Frontend calls POST /users/slots with {day_of_week, start_time, end_time}
    @PostMapping("/slots")
    public ResponseEntity<Map<String, Object>> addSlot(
            @AuthenticationPrincipal Long userId,
            @RequestBody WorkerSlotRequest req) {
        WorkerSlot slot = userService.addSlot(userId, req);
        Map<String, Object> resp = new HashMap<>();
        resp.put("slot", slot);
        return ResponseEntity.ok(resp);
    }

    // Frontend calls DELETE /users/slots/{id}
    @DeleteMapping("/slots/{id}")
    public ResponseEntity<Map<String, String>> removeSlot(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        userService.removeSlot(userId, id);
        return ResponseEntity.ok(Map.of("message", "Slot removed."));
    }

    // ── ORDER HISTORY ─────────────────────────────────────────────
    // Frontend calls GET /users/orders → expects {orders: [...]}
    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> getOrders(
            @AuthenticationPrincipal Long userId) {
        List<Order> orders = orderService.getMine(userId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("orders", orders);
        return ResponseEntity.ok(resp);
    }

    // ── AVATAR UPLOAD ─────────────────────────────────────────────
    // Frontend calls POST /users/avatar with multipart field "avatar"
    @PostMapping("/avatar")
    public ResponseEntity<Map<String, Object>> uploadAvatar(
            @AuthenticationPrincipal Long userId,
            @RequestParam("avatar") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload.");
        }

        // Create uploads/avatars dir if needed
        Path avatarDir = Paths.get(uploadsDir, "avatars");
        Files.createDirectories(avatarDir);

        // Generate unique filename
        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        String filename = userId + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;

        Path dest = avatarDir.resolve(filename);
        Files.write(dest, file.getBytes());

        String avatarUrl = "/uploads/avatars/" + filename;

        // Persist to user record
        ProfileUpdateRequest req = new ProfileUpdateRequest();
        req.setAvatarUrl(avatarUrl);
        userService.updateProfile(userId, req);

        Map<String, Object> resp = new HashMap<>();
        resp.put("avatar_url", avatarUrl);
        return ResponseEntity.ok(resp);
    }
}
