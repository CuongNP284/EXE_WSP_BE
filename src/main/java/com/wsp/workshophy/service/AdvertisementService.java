package com.wsp.workshophy.service;

import com.wsp.workshophy.constant.AdvertisementStatus;
import com.wsp.workshophy.constant.PredefinedRole;
import com.wsp.workshophy.dto.request.AdvertisementRequest;
import com.wsp.workshophy.dto.response.AdvertisementResponse;
import com.wsp.workshophy.entity.Advertisement;
import com.wsp.workshophy.entity.User;
import com.wsp.workshophy.exception.AppException;
import com.wsp.workshophy.exception.ErrorCode;
import com.wsp.workshophy.mapper.AdvertisementMapper;
import com.wsp.workshophy.repository.AdvertisementRepository;
import com.wsp.workshophy.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AdvertisementService {
    AdvertisementRepository advertisementRepository;
    UserRepository userRepository;
    AdvertisementMapper advertisementMapper;

    // ORGANIZER: Tạo quảng cáo
    @PreAuthorize("hasRole('ORGANIZER')")
    public AdvertisementResponse createAdvertisement(AdvertisementRequest request) {
        User currentUser = getCurrentUser();

        // Kiểm tra user có phải ORGANIZER không
        if (!currentUser.getRoles().stream().anyMatch(role -> role.getName().equals(PredefinedRole.ORGANIZER_ROLE))) {
            throw new AppException(ErrorCode.USER_NOT_ORGANIZER);
        }

        Advertisement advertisement = Advertisement.builder()
                .image(request.getImage())
                .duration(request.getDuration())
                .status(AdvertisementStatus.PENDING)
                .organizer(currentUser)
                .build();

        Advertisement savedAdvertisement = advertisementRepository.save(advertisement);
        return advertisementMapper.toAdvertisementResponse(savedAdvertisement);
    }

    // ADMIN: Duyệt quảng cáo (APPROVE hoặc REJECT)
    @PreAuthorize("hasRole('ADMIN')")
    public AdvertisementResponse updateAdvertisementStatus(Long id, String status) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADVERTISEMENT_NOT_FOUND));

        AdvertisementStatus newStatus = AdvertisementStatus.valueOf(status.toUpperCase());
        if (newStatus != AdvertisementStatus.APPROVE && newStatus != AdvertisementStatus.REJECT) {
            throw new AppException(ErrorCode.INVALID_STATUS);
        }

        advertisement.setStatus(newStatus);

        // Nếu APPROVE, tính thời gian hết hạn
        if (newStatus == AdvertisementStatus.APPROVE) {
            LocalDateTime approvedAt = LocalDateTime.now();
            advertisement.setApprovedAt(approvedAt);
            advertisement.setExpirationTime(approvedAt.plusHours(advertisement.getDuration()));
        }

        Advertisement updatedAdvertisement = advertisementRepository.save(advertisement);
        return advertisementMapper.toAdvertisementResponse(updatedAdvertisement);
    }

    // CUSTOMER: Lấy tất cả quảng cáo (ACTIVE, APPROVE, còn hiệu lực)
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<AdvertisementResponse> getAllAdvertisementsForCustomer() {
        LocalDateTime currentTime = LocalDateTime.now();
        return advertisementRepository.findByActiveTrueAndStatusAndExpirationTimeAfter(
                        AdvertisementStatus.APPROVE, currentTime)
                .stream()
                .map(advertisementMapper::toAdvertisementResponse)
                .collect(Collectors.toList());
    }

    // CUSTOMER: Lấy một quảng cáo (ACTIVE, APPROVE, còn hiệu lực)
    @PreAuthorize("hasRole('CUSTOMER')")
    public AdvertisementResponse getAdvertisementForCustomer(Long id) {
        LocalDateTime currentTime = LocalDateTime.now();
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADVERTISEMENT_NOT_FOUND));

        if (!advertisement.getActive() || advertisement.getStatus() != AdvertisementStatus.APPROVE ||
                advertisement.getExpirationTime() == null || advertisement.getExpirationTime().isBefore(currentTime)) {
            throw new AppException(ErrorCode.ADVERTISEMENT_NOT_FOUND);
        }

        return advertisementMapper.toAdvertisementResponse(advertisement);
    }

    // ADMIN: Lấy tất cả quảng cáo (ACTIVE, tất cả trạng thái)
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdvertisementResponse> getAllAdvertisementsForAdmin() {
        return advertisementRepository.findByActiveTrue()
                .stream()
                .map(advertisementMapper::toAdvertisementResponse)
                .collect(Collectors.toList());
    }

    // ADMIN: Lấy một quảng cáo (ACTIVE, tất cả trạng thái)
    @PreAuthorize("hasRole('ADMIN')")
    public AdvertisementResponse getAdvertisementForAdmin(Long id) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADVERTISEMENT_NOT_FOUND));

        if (!advertisement.getActive()) {
            throw new AppException(ErrorCode.ADVERTISEMENT_NOT_FOUND);
        }

        return advertisementMapper.toAdvertisementResponse(advertisement);
    }

    // ORGANIZER: Lấy tất cả quảng cáo của mình (ACTIVE, tất cả trạng thái)
    @PreAuthorize("hasRole('ORGANIZER')")
    public List<AdvertisementResponse> getAllAdvertisementsForOrganizer() {
        User currentUser = getCurrentUser();
        return advertisementRepository.findByOrganizerAndActiveTrue(currentUser)
                .stream()
                .map(advertisementMapper::toAdvertisementResponse)
                .collect(Collectors.toList());
    }

    // ORGANIZER: Lấy một quảng cáo của mình (ACTIVE, tất cả trạng thái)
    @PreAuthorize("hasRole('ORGANIZER')")
    public AdvertisementResponse getAdvertisementForOrganizer(Long id) {
        User currentUser = getCurrentUser();
        Advertisement advertisement = advertisementRepository.findByIdAndOrganizerAndActiveTrue(id, currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.ADVERTISEMENT_NOT_FOUND));

        return advertisementMapper.toAdvertisementResponse(advertisement);
    }

    // ORGANIZER: Xóa quảng cáo của mình
    @PreAuthorize("hasRole('ORGANIZER')")
    public void deleteAdvertisement(Long id) {
        User currentUser = getCurrentUser();
        log.info("User {} is deleting advertisement {}", currentUser.getEmail(), id);
        Advertisement advertisement = advertisementRepository.findByIdAndOrganizerAndActiveTrue(id, currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.ADVERTISEMENT_NOT_FOUND));

        advertisement.setActive(false);
        advertisementRepository.save(advertisement);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmailAndActive(email, true)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}
