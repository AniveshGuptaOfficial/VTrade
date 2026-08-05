package com.vtrade.repository;

import com.vtrade.model.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findTopByPhoneAndUsedFalseOrderByCreatedAtDesc(String phone);
}
