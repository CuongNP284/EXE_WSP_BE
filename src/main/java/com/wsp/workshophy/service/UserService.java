package com.wsp.workshophy.service;

import com.wsp.workshophy.constant.PredefinedRole;
import com.wsp.workshophy.dto.request.User.UserCreationRequest;
import com.wsp.workshophy.dto.request.User.UserUpdateRequest;
import com.wsp.workshophy.dto.response.UserResponse;
import com.wsp.workshophy.entity.Address;
import com.wsp.workshophy.entity.Role;
import com.wsp.workshophy.entity.User;
import com.wsp.workshophy.entity.WorkshopCategory;
import com.wsp.workshophy.exception.AppException;
import com.wsp.workshophy.exception.ErrorCode;
import com.wsp.workshophy.mapper.UserMapper;
import com.wsp.workshophy.repository.AddressRepository;
import com.wsp.workshophy.repository.RoleRepository;
import com.wsp.workshophy.repository.UserRepository;
import com.wsp.workshophy.repository.WorkshopCategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
    private final WorkshopCategoryRepository workshopCategoryRepository;

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

    private User initializeNewUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        String roleName = (request.getRole() != null) ? request.getRole().getName() : PredefinedRole.CUSTOMER_ROLE;

        roleRepository.findById(roleName).ifPresent(roles::add);
        user.setRoles(roles);

        // Xử lý sở thích (interests)
        List<WorkshopCategory> interests = request.getInterestNames().stream()
                .map(this::findWorkshopCategoryByNameAndActive)
                .collect(Collectors.toList());
        user.setInterests(interests);

        return user;
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