package com.vtrade.repository;

import com.vtrade.model.WorkerSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkerSlotRepository extends JpaRepository<WorkerSlot, Long> {
    List<WorkerSlot> findByWorkerId(Long workerId);
    void deleteByWorkerIdAndId(Long workerId, Long id);
}
