package com.wsp.workshophy.repository;

import com.wsp.workshophy.constant.AdvertisementStatus;
import com.wsp.workshophy.entity.Advertisement;
import com.wsp.workshophy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    // Dành cho CUSTOMER: Chỉ lấy quảng cáo ACTIVE, APPROVE, và còn hiệu lực
    List<Advertisement> findByActiveTrueAndStatusAndExpirationTimeAfter(
            AdvertisementStatus status, LocalDateTime currentTime);

    // Dành cho ADMIN: Lấy tất cả quảng cáo ACTIVE
    List<Advertisement> findByActiveTrue();

    // Dành cho ORGANIZER: Lấy tất cả quảng cáo ACTIVE của mình
    List<Advertisement> findByOrganizerAndActiveTrue(User organizer);

    // Tìm quảng cáo theo ID và ORGANIZER
    Optional<Advertisement> findByIdAndOrganizerAndActiveTrue(Long id, User organizer);
}
