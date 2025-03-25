package com.wsp.workshophy.service;

import com.wsp.workshophy.constant.PredefinedRole;
import com.wsp.workshophy.dto.request.RateOrganizerRequest;
import com.wsp.workshophy.dto.request.User.UserCreationRequest;
import com.wsp.workshophy.dto.request.User.UserUpdateRequest;
import com.wsp.workshophy.dto.response.*;
import com.wsp.workshophy.entity.*;
import com.wsp.workshophy.exception.AppException;
import com.wsp.workshophy.exception.ErrorCode;
import com.wsp.workshophy.mapper.UserMapper;
import com.wsp.workshophy.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    WorkshopCategoryRepository workshopCategoryRepository;
    OrganizerProfileRepository organizerProfileRepository;
    RatingRepository ratingRepository;

    public UserResponse createUser(UserCreationRequest request) {
        User user = initializeNewUser(request);

        // Create and set address
        Address address = Address.builder()
                .street(request.getStreet())
                .city(request.getCity())
                .district(request.getDistrict())
                .ward(request.getWard())
                .build();
        user.setAddress(address);

        try {
            return saveAndMapUser(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
    }

    public List<UserResponse> findOrganizersByMatchingCategories() {
        // Lấy user hiện tại và sở thích của họ
        User currentUser = getCurrentUser();
        List<WorkshopCategory> userInterests = currentUser.getInterests();

        if (userInterests == null || userInterests.isEmpty()) {
            return List.of(); // Trả về danh sách rỗng nếu user không có sở thích
        }

        // Lấy tất cả user có role ORGANIZER và có OrganizerProfile
        List<User> organizers = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals(PredefinedRole.ORGANIZER_ROLE)))
                .filter(user -> user.getOrganizerProfile() != null)
                .collect(Collectors.toList());

        // Lọc các organizer có category trùng với sở thích của user hiện tại
        Set<Long> userInterestIds = userInterests.stream()
                .map(WorkshopCategory::getId)
                .collect(Collectors.toSet());

        List<User> matchingOrganizers = organizers.stream()
                .filter(organizer -> {
                    List<WorkshopCategory> organizerCategories = organizer.getOrganizerProfile().getCategories();
                    if (organizerCategories == null || organizerCategories.isEmpty()) {
                        return false;
                    }
                    return organizerCategories.stream()
                            .anyMatch(category -> userInterestIds.contains(category.getId()));
                })
                .toList();

        // Chuyển đổi sang UserResponse
        return matchingOrganizers.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getMyInfo() {
        String email = getCurrentUserEmail();
        log.info("Fetching info for user: {}", email);

        User user = findActiveUserByEmail(email);
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = findActiveUserById(userId);

        User currentUser = getCurrentUser();
        log.info("Current user: {}", currentUser.getEmail());
        log.info("Updating user: {}", user.getEmail());
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !currentUser.getEmail().equals(user.getEmail())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Update basic fields if present
        updateIfPresent(request.getFirstName(), user::setFirstName);
        updateIfPresent(request.getLastName(), user::setLastName);
        updateIfPresent(request.getDob(), user::setDob);
        updateIfPresent(request.getAvatar(), user::setAvatar);
        updateIfPresent(request.getGender(), user::setGender);
        updateIfPresent(request.getPhoneNumber(), user::setPhoneNumber);

        // Update password if present
        updateIfPresent(request.getPassword(),
                password -> user.setPassword(passwordEncoder.encode(password)));

        // Update address if any field is present
        Address address = user.getAddress();
        if (address == null) {
            address = new Address();
            user.setAddress(address);
        }
        updateIfPresent(request.getStreet(), address::setStreet);
        updateIfPresent(request.getCity(), address::setCity);
        updateIfPresent(request.getDistrict(), address::setDistrict);
        updateIfPresent(request.getWard(), address::setWard);

        // Update roles if present
        Optional.ofNullable(request.getRoles())
                .filter(roles -> !roles.isEmpty())
                .ifPresent(roles -> user.setRoles(new HashSet<>(roleRepository.findAllById(roles))));

        // Update interests if present
        Optional.ofNullable(request.getInterestNames())
                .filter(names -> !names.isEmpty())
                .ifPresent(names -> {
                    List<WorkshopCategory> interests = names.stream()
                            .map(this::findWorkshopCategoryByNameAndActive)
                            .collect(Collectors.toList());
                    user.setInterests(interests);
                });

        return saveAndMapUser(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId) {
        User user = findActiveUserById(userId);
        deactivateUser(user);
        // Address will be automatically deleted due to cascade = CascadeType.ALL
    }

    public FollowResponse followUser(String userIdToFollow) {
        User currentUser = getCurrentUser();
        User userToFollow = findActiveUserById(userIdToFollow);

        // Kiểm tra user hiện tại có phải là CUSTOMER
        if (!currentUser.getRoles().stream().anyMatch(role -> role.getName().equals(PredefinedRole.CUSTOMER_ROLE))) {
            throw new AppException(ErrorCode.NOT_CUSTOMER_TO_FOLLOW);
        }

        // Kiểm tra user cần theo dõi có phải là ORGANIZER và có OrganizerProfile
        if (!userToFollow.getRoles().stream().anyMatch(role -> role.getName().equals(PredefinedRole.ORGANIZER_ROLE))) {
            throw new AppException(ErrorCode.USER_NOT_ORGANIZER);
        }
        OrganizerProfile organizerProfile = organizerProfileRepository.findByUserAndActive(userToFollow, true)
                .orElseThrow(() -> new AppException(ErrorCode.ORGANIZER_PROFILE_NOT_FOUND));

        // Kiểm tra xem đã theo dõi chưa
        if (userRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), userIdToFollow)) {
            throw new AppException(ErrorCode.ALREADY_FOLLOWED);
        }

        // Thêm vào danh sách theo dõi
        currentUser.getFollowing().add(userToFollow);
        userToFollow.getFollowers().add(currentUser);

        // Tăng followerCount
        organizerProfile.setFollowerCount(organizerProfile.getFollowerCount() + 1);

        // Lưu thay đổi
        userRepository.save(currentUser);
        userRepository.save(userToFollow);
        organizerProfileRepository.save(organizerProfile);

        return FollowResponse.builder()
                .followerId(currentUser.getId())
                .followingId(userIdToFollow)
                .message("Followed successfully")
                .build();
    }

    public FollowResponse unfollowUser(String userIdToUnfollow) {
        User currentUser = getCurrentUser();
        User userToUnfollow = findActiveUserById(userIdToUnfollow);

        // Kiểm tra user hiện tại có phải là CUSTOMER
        if (!currentUser.getRoles().stream().anyMatch(role -> role.getName().equals(PredefinedRole.CUSTOMER_ROLE))) {
            throw new AppException(ErrorCode.NOT_CUSTOMER_TO_FOLLOW);
        }

        // Kiểm tra user cần bỏ theo dõi có phải là ORGANIZER và có OrganizerProfile
        if (!userToUnfollow.getRoles().stream().anyMatch(role -> role.getName().equals(PredefinedRole.ORGANIZER_ROLE))) {
            throw new AppException(ErrorCode.USER_NOT_ORGANIZER);
        }
        OrganizerProfile organizerProfile = organizerProfileRepository.findByUserAndActive(userToUnfollow, true)
                .orElseThrow(() -> new AppException(ErrorCode.ORGANIZER_PROFILE_NOT_FOUND));

        // Kiểm tra xem có đang theo dõi không
        if (!userRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), userIdToUnfollow)) {
            throw new AppException(ErrorCode.NOT_FOLLOWING_YET);
        }

        // Xóa khỏi danh sách theo dõi
        currentUser.getFollowing().remove(userToUnfollow);
        userToUnfollow.getFollowers().remove(currentUser);

        // Giảm followerCount
        organizerProfile.setFollowerCount(organizerProfile.getFollowerCount() - 1);

        // Lưu thay đổi
        userRepository.save(currentUser);
        userRepository.save(userToUnfollow);
        organizerProfileRepository.save(organizerProfile);

        return FollowResponse.builder()
                .followerId(currentUser.getId())
                .followingId(userIdToUnfollow)
                .message("Unfollowed successfully")
                .build();
    }

    public List<OrganizerProfileNameResponse> getFollowedOrganizerProfiles() {
        User currentUser = getCurrentUser();

        // Kiểm tra user hiện tại có phải là CUSTOMER
        if (!currentUser.getRoles().stream().anyMatch(role -> role.getName().equals(PredefinedRole.CUSTOMER_ROLE))) {
            throw new AppException(ErrorCode.ONLY_CUSTOMER_CAN_VIEW_FOLLOWED_ORGANIZERS);
        }

        return currentUser.getFollowing().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals(PredefinedRole.ORGANIZER_ROLE)))
                .map(user -> organizerProfileRepository.findByUserAndActive(user, true)
                        .orElseThrow(() -> new AppException(ErrorCode.ORGANIZER_PROFILE_NOT_FOUND)))
                .map(profile -> OrganizerProfileNameResponse.builder()
                        .name(profile.getName())
                        .build())
                .collect(Collectors.toList());
    }

    public List<FollowerUsernameResponse> getFollowersForOrganizerProfile() {
        User currentUser = getCurrentUser();

        // Kiểm tra user có phải là ORGANIZER và có OrganizerProfile
        if (!currentUser.getRoles().stream().anyMatch(role -> role.getName().equals(PredefinedRole.ORGANIZER_ROLE))) {
            throw new AppException(ErrorCode.USER_NOT_ORGANIZER);
        }
        organizerProfileRepository.findByUserAndActive(currentUser, true)
                .orElseThrow(() -> new AppException(ErrorCode.ORGANIZER_PROFILE_NOT_FOUND));

        return currentUser.getFollowers().stream()
                .map(user -> FollowerUsernameResponse.builder()
                        .username(user.getUsername())
                        .build())
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        log.info("Fetching all active users");
        return userRepository.findAllByActive(true)
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(findActiveUserById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    public List<UserResponse> searchUsersByUsername(String username) {

        List<User> users = userRepository.findByUsernameContainingIgnoreCaseAndActive(username, true);
        return users.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    private User initializeNewUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        String roleName = (request.getRole() != null) ? request.getRole().getName() : PredefinedRole.CUSTOMER_ROLE;

        roleRepository.findById(roleName).ifPresent(roles::add);
        user.setRoles(roles);

        // Xử lý sở thích (interests) - Kiểm tra null
        List<WorkshopCategory> interests = Optional.ofNullable(request.getInterestNames())
                .map(names -> names.stream()
                        .map(this::findWorkshopCategoryByNameAndActive)
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
        user.setInterests(interests);

        return user;
    }

    public RatingResponse rateOrganizerProfile(String organizerUserId, RateOrganizerRequest request) {
        User currentUser = getCurrentUser();

        // Kiểm tra user hiện tại có phải là CUSTOMER
        if (!currentUser.getRoles().stream().anyMatch(role -> role.getName().equals(PredefinedRole.CUSTOMER_ROLE))) {
            throw new AppException(ErrorCode.NOT_CUSTOMER_TO_RATE);
        }

        // Kiểm tra user được đánh giá có phải là ORGANIZER và có OrganizerProfile
        User organizer = findActiveUserById(organizerUserId);
        if (!organizer.getRoles().stream().anyMatch(role -> role.getName().equals(PredefinedRole.ORGANIZER_ROLE))) {
            throw new AppException(ErrorCode.USER_NOT_ORGANIZER);
        }
        OrganizerProfile organizerProfile = organizerProfileRepository.findByUserAndActive(organizer, true)
                .orElseThrow(() -> new AppException(ErrorCode.ORGANIZER_PROFILE_NOT_FOUND));

        // Kiểm tra rating hợp lệ (1-5)
        if (request.getRating() < 1.0 || request.getRating() > 5.0) {
            throw new AppException(ErrorCode.INVALID_RATING);
        }

        // Kiểm tra xem user đã đánh giá OrganizerProfile này chưa
        Optional<Rating> existingRating = ratingRepository.findByUserAndOrganizerProfile(currentUser, organizerProfile);        Rating ratingEntity;

        if (existingRating.isPresent()) {
            // Nếu đã đánh giá, cập nhật rating mới
            ratingEntity = existingRating.get();
            ratingEntity.setRating(request.getRating());
            ratingEntity.setComment(request.getComment());
        } else {
            // Nếu chưa đánh giá, tạo rating mới
            ratingEntity = Rating.builder()
                    .user(currentUser)
                    .organizerProfile(organizerProfile)
                    .rating(request.getRating())
                    .comment(request.getComment())
                    .build();
            organizerProfile.getRatings().add(ratingEntity);
        }

        // Lưu rating
        ratingRepository.save(ratingEntity);

        // Tính lại averageRating
        updateAverageRating(organizerProfile);

        return RatingResponse.builder()
                .id(ratingEntity.getId())
                .username(currentUser.getUsername())
                .organizerProfileId(organizerProfile.getId())
                .organizerProfileName(organizerProfile.getName())
                .rating(ratingEntity.getRating())
                .createdDate(ratingEntity.getCreatedDate())
                .comment(ratingEntity.getComment())
                .build();
    }

    public List<UserRatedOrganizerResponse> getRatedOrganizerProfilesByUser() {
        User currentUser = getCurrentUser();

        // Kiểm tra user hiện tại có phải là CUSTOMER
        if (!currentUser.getRoles().stream().anyMatch(role -> role.getName().equals(PredefinedRole.CUSTOMER_ROLE))) {
            throw new AppException(ErrorCode.NOT_CUSTOMER_TO_VIEW_RATED);
        }

        List<Rating> ratings = ratingRepository.findByUser(currentUser);
        return ratings.stream()
                .map(rating -> UserRatedOrganizerResponse.builder()
                        .organizerProfileId(rating.getOrganizerProfile().getId())
                        .organizerProfileName(rating.getOrganizerProfile().getName())
                        .rating(rating.getRating())
                        .comment(rating.getComment())
                        .createdDate(rating.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
    }

    public List<OrganizerRatedByUserResponse> getRatingsForOrganizerProfile() {
        User currentUser = getCurrentUser();

        // Kiểm tra user có phải là ORGANIZER và có OrganizerProfile
        if (!currentUser.getRoles().stream().anyMatch(role -> role.getName().equals(PredefinedRole.ORGANIZER_ROLE))) {
            throw new AppException(ErrorCode.USER_NOT_ORGANIZER);
        }
        OrganizerProfile organizerProfile = organizerProfileRepository.findByUserAndActive(currentUser, true)
                .orElseThrow(() -> new AppException(ErrorCode.ORGANIZER_PROFILE_NOT_FOUND));

        List<Rating> ratings = ratingRepository.findByOrganizerProfile(organizerProfile);
        return ratings.stream()
                .map(rating -> OrganizerRatedByUserResponse.builder()
                        .username(rating.getUser().getUsername())
                        .rating(rating.getRating())
                        .comment(rating.getComment())
                        .createdDate(rating.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
    }

    private void updateAverageRating(OrganizerProfile organizerProfile) {
        List<Rating> ratings = ratingRepository.findByOrganizerProfile(organizerProfile);
        if (ratings.isEmpty()) {
            organizerProfile.setAverageRating(0.0);
        } else {
            double average = ratings.stream()
                    .mapToDouble(Rating::getRating)
                    .average()
                    .orElse(0.0);
            organizerProfile.setAverageRating(average);
        }
        organizerProfileRepository.save(organizerProfile);
    }

    private <T> void updateIfPresent(T value, java.util.function.Consumer<T> setter) {
        if (value != null && (!(value instanceof String) || !((String) value).isEmpty())) {
            setter.accept(value);
        }
    }

    private UserResponse saveAndMapUser(User user) {
        return userMapper.toUserResponse(userRepository.save(user));
    }

    private User findActiveUserById(String userId) {
        return userRepository.findByIdAndActive(userId, true)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    private WorkshopCategory findWorkshopCategoryByNameAndActive(String name) {
        return workshopCategoryRepository.findByNameAndActive(name, true)
                .orElseThrow(() -> new AppException(ErrorCode.WORKSHOP_CATEGORY_NOT_FOUND));
    }

    private User findActiveUserByEmail(String email) {
        return userRepository.findByEmailAndActive(email, true)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private void deactivateUser(User user) {
        user.setActive(false);

        Address address = user.getAddress();
        if (address != null) {
            address.setActive(false);
        }

        userRepository.save(user);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmailAndActive(email, true)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}