package com.wsp.workshophy.repository;

import com.wsp.workshophy.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByEmailAndOtpCodeAndUsedFalseAndExpiresAtAfter(
            String email, String otpCode, LocalDateTime currentTime);
}
