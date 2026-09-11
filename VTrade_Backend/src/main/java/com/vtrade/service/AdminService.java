package com.vtrade.service;

import com.vtrade.dto.ProvisionUserRequest;
import com.vtrade.model.User;
import com.vtrade.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a Delivery Staff or Partner Store account with a temporary password.
     * The account is marked mustResetPassword=true, so the very first successful
     * login (by password OR by matching Google account) forces the user to
     * PUT /api/users/password before continuing to use the platform normally.
     */
    public User provisionUser(ProvisionUserRequest req) {
        String normalizedEmail = req.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        User user = new User();
        user.setFirstName(req.getFirstName() != null && !req.getFirstName().isBlank() ? req.getFirstName() : "New");
        user.setLastName(req.getLastName() != null && !req.getLastName().isBlank() ? req.getLastName() : "User");
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(req.getTempPassword()));
        user.setRole(req.getRole());
        user.setMustResetPassword(true);

        if ("delivery_staff".equals(req.getRole())) {
            user.setWorker(true);
            user.setWorkerType("delivery_staff");
            user.setVerificationStatus("approved");
        }

        return userRepository.save(user);
    }
}