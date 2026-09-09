package com.vtrade.service;

import com.vtrade.dto.AccessRequestDto;
import com.vtrade.model.AccessRequest;
import com.vtrade.repository.AccessRequestRepository;
import org.springframework.stereotype.Service;

@Service
public class AccessRequestService {

    private final AccessRequestRepository accessRequestRepository;

    public AccessRequestService(AccessRequestRepository accessRequestRepository) {
        this.accessRequestRepository = accessRequestRepository;
    }

    public AccessRequest submit(AccessRequestDto dto) {
        AccessRequest req = new AccessRequest();
        req.setName(dto.getName().trim());
        req.setEmail(dto.getEmail().trim().toLowerCase());
        req.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);
        req.setRequestedRole(dto.getRequestedRole());
        req.setMessage(dto.getMessage());
        req.setStatus("pending");
        return accessRequestRepository.save(req);
    }
}