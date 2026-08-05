package com.vtrade.service;

import com.vtrade.dto.*;
import com.vtrade.model.OtpToken;
import com.vtrade.model.User;
import com.vtrade.repository.OtpTokenRepository;
import com.vtrade.repository.UserRepository;
import com.vtrade.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${vtrade.otp.demo-mode}")
    private boolean demoMode;

    @Value("${vtrade.otp.expiry-minutes}")
    private int otpExpiryMinutes;

    public AuthService(UserRepository userRepository,
                        OtpTokenRepository otpTokenRepository,
                        PasswordEncoder passwordEncoder,
                        JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.otpTokenRepository = otpTokenRepository;
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

    /** Sends (generates) an OTP for the given phone. In demo mode the OTP is returned to the caller. */
    public String sendOtp(String phone) {
        String code = String.format("%06d", new Random().nextInt(1_000_000));

        OtpToken token = new OtpToken();
        token.setPhone(phone);
        token.setCode(code);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes));
        otpTokenRepository.save(token);

        return demoMode ? code : null;
    }

    /** Verifies the OTP. If valid, finds-or-creates the user and returns an auth response. */
    public AuthResponse verifyOtp(String phone, String code) {
        OtpToken token = otpTokenRepository.findTopByPhoneAndUsedFalseOrderByCreatedAtDesc(phone)
                .orElseThrow(() -> new IllegalArgumentException("No OTP was requested for this phone number."));

        if (token.isUsed()) {
            throw new IllegalArgumentException("This OTP has already been used.");
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP has expired. Please request a new one.");
        }
        if (!token.getCode().equals(code)) {
            throw new IllegalArgumentException("Incorrect OTP.");
        }

        token.setUsed(true);
        otpTokenRepository.save(token);

        User user = userRepository.findByPhone(phone).orElseGet(() -> {
            User u = new User();
            u.setFirstName("Student");
            u.setLastName("");
            u.setPhone(phone);
            u.setRole("buyer");
            return userRepository.save(u);
        });

        String jwt = jwtUtil.generateToken(user.getId(), user.getPhone(), user.getRole());
        return new AuthResponse(jwt, user);
    }
}
