package com.vtrade.pickup;

import com.vtrade.pickup.PickupRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PickupRequestRepository extends JpaRepository<PickupRequest, Long> {
    List<PickupRequest> findByRequesterIdOrderByCreatedAtDesc(Long requesterId);
    List<PickupRequest> findByStatus(String status);
    List<PickupRequest> findByAssignedWorkerId(Long workerId);
}
