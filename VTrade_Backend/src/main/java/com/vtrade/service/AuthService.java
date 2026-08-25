package com.vtrade.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.vtrade.dto.*;
import com.vtrade.model.User;
import com.vtrade.repository.UserRepository;
import com.vtrade.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.regex.Pattern;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${vtrade.google.client-id}")
    private String googleClientId;

    /** firstname.lastname + 4-digit batch year — VIT student email */
    private static final Pattern VIT_STUDENT_EMAIL =
            Pattern.compile("^[a-z]+\\.[a-z]+[0-9]{4}@vitstudent\\.ac\\.in$");

    /** firstname.lastname — VIT staff/faculty email */
    private static final Pattern VIT_STAFF_EMAIL =
            Pattern.compile("^[a-z]+\\.[a-z]+@vit\\.ac\\.in$");

    public AuthService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByPhone(req.getPhone())) {
            throw new IllegalArgumentException("An account with this phone number already exists.");
        }
        if (req.getEmail() != null && !req.getEmail().isBlank() && userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole("buyer");

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getPhone(), user.getRole());
        return new AuthResponse(token, user);
    }

    public AuthResponse login(LoginRequest req) {
        User user;
        if (req.getStudentId() != null && !req.getStudentId().isBlank()) {
            user = userRepository.findByStudentId(req.getStudentId())
                    .orElseThrow(() -> new IllegalArgumentException("No account found with this registration number. Please register first."));
        } else if (req.getEmail() != null && !req.getEmail().isBlank()) {
            user = userRepository.findByEmail(req.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("No account found with this email."));
        } else if (req.getPhone() != null && !req.getPhone().isBlank()) {
            user = userRepository.findByPhone(req.getPhone())
                    .orElseThrow(() -> new IllegalArgumentException("No account found with this phone number."));
        } else {
            throw new IllegalArgumentException("Email, phone, or registration number is required.");
        }

        if (user.getPasswordHash() == null || !passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Incorrect password.");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getPhone(), user.getRole());
        return new AuthResponse(token, user);
    }

    /**
     * Verifies a Google ID token, restricts sign-in to official VIT email addresses,
     * then finds an existing user by googleId or email, or creates a new one.
     * Returns the same AuthResponse shape as every other login path.
     */
    public AuthResponse googleLogin(String idTokenString) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not verify Google token.");
        }
        if (idToken == null) {
            throw new IllegalArgumentException("Invalid or expired Google token.");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Your Google account has no email address on file.");
        }

        String normalizedEmail = email.trim().toLowerCase();
        boolean isStudent = VIT_STUDENT_EMAIL.matcher(normalizedEmail).matches();
        boolean isStaff = VIT_STAFF_EMAIL.matcher(normalizedEmail).matches();

        if (!isStudent && !isStaff) {
            throw new IllegalArgumentException(
                    "Please sign in with your official VIT email (@vitstudent.ac.in or @vit.ac.in).");
        }

        User user = userRepository.findByGoogleId(googleId)
                .or(() -> userRepository.findByEmail(normalizedEmail))
                .orElseGet(() -> {
                    User u = new User();
                    u.setFirstName(firstName != null ? firstName : "Student");
                    u.setLastName(lastName != null ? lastName : "");
                    u.setEmail(normalizedEmail);
                    u.setRole("buyer");
                    return u;
                });

        user.setGoogleId(googleId);
        if (user.getEmail() == null) {
            user.setEmail(normalizedEmail);
        }
        user = userRepository.save(user);

        String jwt = jwtUtil.generateToken(user.getId(), user.getPhone(), user.getRole());
        return new AuthResponse(jwt, user);
    }
}