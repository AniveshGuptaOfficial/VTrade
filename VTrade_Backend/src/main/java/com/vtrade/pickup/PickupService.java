package com.vtrade.pickup;

import com.vtrade.pickup.PickupRequestDto;
import com.vtrade.pickup.PickupRequest;
import com.vtrade.auth.User;
import com.vtrade.pickup.PickupRequestRepository;
import com.vtrade.auth.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PickupService {

    private static final double BASE_FEE = 10.0;
    private static final double DISTANCE_FEE = 5.0;
    private static final double COD_SURCHARGE = 5.0;

    private final PickupRequestRepository pickupRequestRepository;
    private final UserRepository userRepository;

    public PickupService(PickupRequestRepository pickupRequestRepository, UserRepository userRepository) {
        this.pickupRequestRepository = pickupRequestRepository;
        this.userRepository = userRepository;
    }

    public PickupRequest create(Long userId, PickupRequestDto req) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        double fee = BASE_FEE + DISTANCE_FEE;
        if ("cod".equalsIgnoreCase(req.getPaymentMode())) {
            fee += COD_SURCHARGE;
        }

        PickupRequest pr = new PickupRequest();
        pr.setRequesterId(userId);
        pr.setKartName(req.getKartName());
        pr.setProductDetails(req.getProductDetails());
        pr.setTrackingId(req.getTrackingId());
        pr.setDeliveryBoyName(req.getDeliveryBoyName());
        pr.setDeliveryBoyPhone(req.getDeliveryBoyPhone());
        pr.setAmount(req.getAmount());
        pr.setPaymentMode(req.getPaymentMode());
        pr.setRequesterPhone(req.getRequesterPhone());
        pr.setHostelBlock(req.getHostelBlock());
        pr.setRoomNumber(req.getRoomNumber());
        pr.setNotes(req.getNotes());
        pr.setFeeEstimate(fee);
        pr.setStatus("pending");

        return pickupRequestRepository.save(pr);
    }

    public List<PickupRequest> getMine(Long userId) {
        return pickupRequestRepository.findByRequesterIdOrderByCreatedAtDesc(userId);
    }

    public List<PickupRequest> getOpenForWorkers() {
        return pickupRequestRepository.findByStatus("pending");
    }

    public PickupRequest accept(Long workerId, Long requestId) {
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found."));

        PickupRequest pr = pickupRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Pickup request not found."));

        if (!"pending".equals(pr.getStatus())) {
            throw new IllegalArgumentException("This request has already been accepted.");
        }

        pr.setStatus("assigned");
        pr.setAssignedWorkerId(worker.getId());
        pr.setAssignedWorkerName(worker.getFirstName());
        return pickupRequestRepository.save(pr);
    }

    public PickupRequest updateStatus(Long userId, Long requestId, String status) {
        PickupRequest pr = pickupRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Pickup request not found."));

        boolean isOwner = pr.getRequesterId().equals(userId);
        boolean isWorker = pr.getAssignedWorkerId() != null && pr.getAssignedWorkerId().equals(userId);

        if (!isOwner && !isWorker) {
            throw new IllegalArgumentException("You are not authorized to update this request.");
        }

        pr.setStatus(status);

        // Award bounty points to the worker on delivery
        if ("delivered".equals(status) && pr.getAssignedWorkerId() != null) {
            User worker = userRepository.findById(pr.getAssignedWorkerId()).orElse(null);
            if (worker != null) {
                worker.setBountyPoints((worker.getBountyPoints() == null ? 0 : worker.getBountyPoints()) + 10);
                userRepository.save(worker);
            }
        }

        return pickupRequestRepository.save(pr);
    }
}
