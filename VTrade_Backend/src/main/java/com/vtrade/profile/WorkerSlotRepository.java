package com.vtrade.profile;

import com.vtrade.profile.WorkerSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkerSlotRepository extends JpaRepository<WorkerSlot, Long> {
    List<WorkerSlot> findByWorkerId(Long workerId);
    void deleteByWorkerIdAndId(Long workerId, Long id);
}
