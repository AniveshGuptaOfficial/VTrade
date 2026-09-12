package com.vtrade.admin;

import com.vtrade.admin.AccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {
}