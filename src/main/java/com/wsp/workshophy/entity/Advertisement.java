package com.wsp.workshophy.entity;

import com.wsp.workshophy.base.BaseEntity;
import com.wsp.workshophy.constant.AdvertisementStatus;
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
@Table(name = "advertisements")
public class Advertisement extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "image", nullable = false)
    String image; // URL hoặc đường dẫn đến hình ảnh quảng cáo

    @Column(name = "duration", nullable = false)
    Long duration; // Thời hạn (tính bằng giờ)

    @Column(name = "expiration_time")
    LocalDateTime expirationTime; // Thời gian hết hạn

    @Column(name = "approved_at")
    LocalDateTime approvedAt; // Thời gian được duyệt

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    AdvertisementStatus status; // Trạng thái: PENDING, APPROVE, REJECT

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    User organizer; // Người tạo quảng cáo (ORGANIZER)
}
