package com.wsp.workshophy.service;

import com.wsp.workshophy.constant.PredefinedRole;
import com.wsp.workshophy.dto.request.OrganizerProfile.OrganizerProfileCreationRequest;
import com.wsp.workshophy.dto.request.OrganizerProfile.OrganizerProfileUpdateRequest;
import com.wsp.workshophy.dto.response.OrganizerProfileResponse;
import com.wsp.workshophy.dto.response.OrganizerRatedByUserResponse;
import com.wsp.workshophy.entity.OrganizerProfile;
import com.wsp.workshophy.entity.Rating;
import com.wsp.workshophy.entity.User;
import com.wsp.workshophy.entity.WorkshopCategory;
import com.wsp.workshophy.exception.AppException;
import com.wsp.workshophy.exception.ErrorCode;
import com.wsp.workshophy.mapper.OrganizerProfileMapper;
import com.wsp.workshophy.repository.OrganizerProfileRepository;
import com.wsp.workshophy.repository.RatingRepository;
import com.wsp.workshophy.repository.UserRepository;
import com.wsp.workshophy.repository.WorkshopCategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrganizerProfileService {
    OrganizerProfileRepository organizerProfileRepository;
    UserRepository userRepository;
    WorkshopCategoryRepository workshopCategoryRepository;
    OrganizerProfileMapper organizerProfileMapper;
    RatingRepository ratingRepository;

    public OrganizerProfileResponse createOrganizerProfile(OrganizerProfileCreationRequest request) {
        User user = getCurrentUser();
        checkOrganizerRole(user);

        organizerProfileRepository.findByUserAndActive(user, true)
                .ifPresent(profile -> {
                    throw new AppException(ErrorCode.ORGANIZER_PROFILE_ALREADY_EXISTS);
                });

        OrganizerProfile organizerProfile = organizerProfileMapper.toOrganizerProfile(request);
        organizerProfile.setUser(user);
        organizerProfile.setCreatedDate(organizerProfile.getCreatedDate());
        organizerProfile.setFollowerCount(0);
        organizerProfile.setWorkshopCount(0);

        // Lấy danh sách WorkshopCategory từ categoryNames
        List<WorkshopCategory> categories = request.getCategoryNames().stream()
                .map(this::findWorkshopCategoryByNameAndActive)
                .collect(Collectors.toList());
        organizerProfile.setCategories(categories);

        OrganizerProfile savedProfile = organizerProfileRepository.save(organizerProfile);
        return organizerProfileMapper.toOrganizerProfileResponse(savedProfile);
    }

    public OrganizerProfileResponse updateOrganizerProfile(Long id, OrganizerProfileUpdateRequest request) {
        OrganizerProfile organizerProfile = findOrganizerProfileByIdAndActive(id);
        checkOrganizerAccess(organizerProfile);

        // Chỉ cập nhật các trường nếu chúng có giá trị
        Optional.ofNullable(request.getName()).ifPresent(organizerProfile::setName);
        Optional.ofNullable(request.getAvatar()).ifPresent(organizerProfile::setAvatar);
        Optional.ofNullable(request.getDescription()).ifPresent(organizerProfile::setDescription);
        Optional.ofNullable(request.getTheme()).ifPresent(organizerProfile::setTheme);
        Optional.ofNullable(request.getFollowerCount()).ifPresent(organizerProfile::setFollowerCount);
        Optional.ofNullable(request.getWorkshopCount()).ifPresent(organizerProfile::setWorkshopCount);
        Optional.ofNullable(request.getEstablishmentDate()).ifPresent(organizerProfile::setEstablishmentDate);

        // Cập nhật danh sách WorkshopCategory nếu có
        Optional.ofNullable(request.getCategoryNames()).ifPresent(categoryNames -> {
            List<WorkshopCategory> categories = categoryNames.stream()
                    .map(this::findWorkshopCategoryByNameAndActive)
                    .collect(Collectors.toList());
            organizerProfile.setCategories(categories);
        });

        return organizerProfileMapper.toOrganizerProfileResponse(organizerProfileRepository.save(organizerProfile));
    }

    public OrganizerProfileResponse getOrganizerProfileByUserId(String userId) {
        OrganizerProfile organizerProfile = organizerProfileRepository.findByUserIdAndActive(userId, true)
                .orElseThrow(() -> new AppException(ErrorCode.ORGANIZER_PROFILE_NOT_FOUND));
        return organizerProfileMapper.toOrganizerProfileResponse(organizerProfile);
    }

    public List<OrganizerProfileResponse> searchOrganizerProfilesByName(String name) {
        List<OrganizerProfile> profiles = organizerProfileRepository.findByNameContainingIgnoreCaseAndActive(name, true);
        return profiles.stream()
                .map(organizerProfileMapper::toOrganizerProfileResponse)
                .collect(Collectors.toList());
    }


    public List<OrganizerProfileResponse> getAllOrganizerProfiles() {
        return organizerProfileRepository.findAllByActive(true).stream()
                .map(organizerProfileMapper::toOrganizerProfileResponse)
                .toList();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    public OrganizerProfileResponse getOrganizerProfileForUser(Long id) {
        // Tìm OrganizerProfile theo id
        OrganizerProfile organizerProfile = organizerProfileRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new AppException(ErrorCode.ORGANIZER_PROFILE_NOT_FOUND));

        // Chuyển đổi sang OrganizerProfileResponse
        OrganizerProfileResponse response = organizerProfileMapper.toOrganizerProfileResponse(organizerProfile);

        // Tìm các OrganizerProfile liên quan
        List<OrganizerProfile> relatedProfiles = findRelatedOrganizerProfiles(organizerProfile);
        List<OrganizerProfileResponse> relatedProfileResponses = relatedProfiles.stream()
                .map(organizerProfileMapper::toOrganizerProfileResponse)
                .collect(Collectors.toList());

        // Gán danh sách liên quan vào response
        response.setRelatedProfiles(relatedProfileResponses);

        // Lấy danh sách ratings của OrganizerProfile
        List<Rating> ratings = ratingRepository.findByOrganizerProfile(organizerProfile);
        List<OrganizerRatedByUserResponse> ratingResponses = ratings.stream()
                .map(rating -> OrganizerRatedByUserResponse.builder()
                        .username(rating.getUser().getUsername())
                        .rating(rating.getRating())
                        .comment(rating.getComment())
                        .createdDate(rating.getCreatedDate())
                        .build())
                .collect(Collectors.toList());

        // Gán danh sách ratings vào response
        response.setRatings(ratingResponses);

        return response;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    public OrganizerProfileResponse getOrganizerProfile(Long id) {
// Tìm OrganizerProfile theo id
        OrganizerProfile organizerProfile = findOrganizerProfileByIdAndActive(id);

        // Chuyển đổi sang OrganizerProfileResponse
        OrganizerProfileResponse response = organizerProfileMapper.toOrganizerProfileResponse(organizerProfile);

        // Lấy danh sách ratings của OrganizerProfile
        List<Rating> ratings = ratingRepository.findByOrganizerProfile(organizerProfile);
        List<OrganizerRatedByUserResponse> ratingResponses = ratings.stream()
                .map(rating -> OrganizerRatedByUserResponse.builder()
                        .username(rating.getUser().getUsername())
                        .rating(rating.getRating())
                        .comment(rating.getComment())
                        .createdDate(rating.getCreatedDate())
                        .build())
                .collect(Collectors.toList());

        // Gán danh sách ratings vào response
        response.setRatings(ratingResponses);

        // Không lấy relatedProfiles trong API này
        response.setRelatedProfiles(null);

        return response;
    }

    public OrganizerProfileResponse getMyOrganizerProfile() {
        User user = getCurrentUser();
        checkOrganizerRole(user);
        OrganizerProfile organizerProfile = organizerProfileRepository.findByUserAndActive(user, true)
                .orElseThrow(() -> new AppException(ErrorCode.ORGANIZER_PROFILE_NOT_FOUND));
        return organizerProfileMapper.toOrganizerProfileResponse(organizerProfile);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOrganizerProfile(Long id) {
        OrganizerProfile organizerProfile = findOrganizerProfileByIdAndActive(id);
        organizerProfile.setActive(false);
        organizerProfileRepository.save(organizerProfile);
    }



    private List<OrganizerProfile> findRelatedOrganizerProfiles(OrganizerProfile organizerProfile) {
        Set<OrganizerProfile> relatedProfiles = new HashSet<>();

        // 1. Tìm các OrganizerProfile có cùng category
        for (WorkshopCategory category : organizerProfile.getCategories()) {
            List<OrganizerProfile> profilesByCategory = organizerProfileRepository
                    .findByCategoryAndActiveTrueAndNotId(category, organizerProfile.getId());
            relatedProfiles.addAll(profilesByCategory);
        }

        // 2. Nếu user hiện tại là CUSTOMER, thêm các OrganizerProfile thuộc sở thích của họ
        User currentUser = getCurrentUser();
        if (currentUser.getRoles().stream().anyMatch(role -> role.getName().equals(PredefinedRole.CUSTOMER_ROLE))) {
            for (WorkshopCategory interest : currentUser.getInterests()) {
                List<OrganizerProfile> profilesByInterest = organizerProfileRepository
                        .findByCategoryAndActiveTrueAndNotId(interest, organizerProfile.getId());
                relatedProfiles.addAll(profilesByInterest);
            }
        }

        return new ArrayList<>(relatedProfiles);
    }

    private OrganizerProfile findOrganizerProfileByIdAndActive(Long id) {
        return organizerProfileRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new AppException(ErrorCode.ORGANIZER_PROFILE_NOT_FOUND));
    }

    private WorkshopCategory findWorkshopCategoryByNameAndActive(String name) {
        return workshopCategoryRepository.findByNameAndActive(name, true)
                .orElseThrow(() -> new AppException(ErrorCode.WORKSHOP_CATEGORY_NOT_FOUND));
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmailAndActive(username, true)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    private void checkOrganizerRole(User user) {
        boolean hasOrganizerRole = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ORGANIZER"));
        if (!hasOrganizerRole) {
            throw new AppException(ErrorCode.USER_NOT_ORGANIZER);
        }
    }

    private void checkOrganizerAccess(OrganizerProfile organizerProfile) {
        User currentUser = getCurrentUser();
        if (!isAdmin() && !organizerProfile.getUser().getUsername().equals(currentUser.getUsername())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }
}
