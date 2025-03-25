package com.wsp.workshophy.entity;

import com.wsp.workshophy.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "otps")
public class Otp extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "otp_code", nullable = false)
    String otpCode;

    @Column(name = "email", nullable = false)
    String email;

    @Column(name = "issued_at", nullable = false)
    LocalDateTime issuedAt;

    @Column(name = "expires_at", nullable = false)
    LocalDateTime expiresAt;

    @Column(name = "used", nullable = false)
    boolean used = false;
}
