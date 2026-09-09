package com.vtrade.service;

import com.vtrade.dto.BecomeWorkerRequest;
import com.vtrade.dto.ProfileUpdateRequest;
import com.vtrade.dto.WorkerSlotRequest;
import com.vtrade.model.User;
import com.vtrade.model.WorkerSlot;
import com.vtrade.repository.UserRepository;
import com.vtrade.repository.WorkerSlotRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final WorkerSlotRepository workerSlotRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       WorkerSlotRepository workerSlotRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.workerSlotRepository = workerSlotRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    public User updateProfile(Long userId, ProfileUpdateRequest req) {
        User user = getProfile(userId);
        if (req.getFirstName() != null) user.setFirstName(req.getFirstName());
        if (req.getLastName() != null) user.setLastName(req.getLastName());
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        if (req.getPhone() != null && !req.getPhone().isBlank()) {
            String phone = req.getPhone().trim();
            if (!phone.matches("\\d{10}")) {
                throw new IllegalArgumentException("Phone must be exactly 10 digits.");
            }
            if (!phone.equals(user.getPhone()) && userRepository.existsByPhone(phone)) {
                throw new IllegalArgumentException("An account with this phone number already exists.");
            }
            user.setPhone(phone);
        }
        if (req.getStudentId() != null) user.setStudentId(req.getStudentId());
        if (req.getHostelBlock() != null) user.setHostelBlock(req.getHostelBlock());
        if (req.getRoomNumber() != null) user.setRoomNumber(req.getRoomNumber());
        if (req.getAvatarUrl() != null) user.setAvatarUrl(req.getAvatarUrl());
        return userRepository.save(user);
    }

    /** Buyer applies to become a worker. Sets role/worker flag and stores application details. */
    public User becomeWorker(Long userId, BecomeWorkerRequest req) {
        User user = getProfile(userId);
        user.setWorker(true);
        // Map workerType to role string the frontend expects
        user.setRole("student".equalsIgnoreCase(req.getWorkerType()) ? "student_worker" : "delivery_staff");
        user.setWorkerType(req.getWorkerType());
        user.setStudentId(req.getStudentId());
        user.setBio(req.getBio());
        user.setVerificationStatus("pending");
        return userRepository.save(user);
    }

    public User toggleOnline(Long userId, boolean online) {
        User user = getProfile(userId);
        if (!user.isWorker()) {
            throw new IllegalArgumentException("Only registered workers can toggle online status.");
        }
        user.setWorkerOnline(online);
        return userRepository.save(user);
    }

    public List<WorkerSlot> getSlots(Long userId) {
        return workerSlotRepository.findByWorkerId(userId);
    }

    public WorkerSlot addSlot(Long userId, WorkerSlotRequest req) {
        User user = getProfile(userId);
        if (!user.isWorker()) {
            throw new IllegalArgumentException("Only registered workers can add availability slots.");
        }
        WorkerSlot slot = new WorkerSlot();
        slot.setWorkerId(userId);
        slot.setDayOfWeek(req.getDayOfWeek());
        slot.setStartTime(req.getStartTime());
        slot.setEndTime(req.getEndTime());
        return workerSlotRepository.save(slot);
    }

    public void removeSlot(Long userId, Long slotId) {
        workerSlotRepository.deleteByWorkerIdAndId(userId, slotId);
    }

    /**
     * Set or change the user's password. Also clears mustResetPassword, since this
     * is the endpoint an admin-provisioned Delivery Staff / Partner Store account
     * calls to complete their forced first-time password reset.
     */
    public User setPassword(Long userId, String rawPassword) {
        User user = getProfile(userId);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setMustResetPassword(false);
        return userRepository.save(user);
    }
}