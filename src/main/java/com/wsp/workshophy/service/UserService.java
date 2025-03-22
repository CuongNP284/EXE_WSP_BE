package com.wsp.workshophy.service;

import com.wsp.workshophy.constant.PredefinedRole;
import com.wsp.workshophy.dto.request.UserCreationRequest;
import com.wsp.workshophy.dto.request.UserUpdateRequest;
import com.wsp.workshophy.dto.response.UserResponse;
import com.wsp.workshophy.entity.Address;
import com.wsp.workshophy.entity.Role;
import com.wsp.workshophy.entity.User;
import com.wsp.workshophy.exception.AppException;
import com.wsp.workshophy.exception.ErrorCode;
import com.wsp.workshophy.mapper.UserMapper;
import com.wsp.workshophy.repository.AddressRepository;
import com.wsp.workshophy.repository.RoleRepository;
import com.wsp.workshophy.repository.UserRepository;
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
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    AddressRepository addressRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

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

    @PostAuthorize("returnObject.username == authentication.name or hasRole('ADMIN')")
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = findActiveUserById(userId);

        // Update user basic fields
        userMapper.updateUserFromRequest(request, user);

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
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            user.setRoles(new HashSet<>(roleRepository.findAllById(request.getRoles())));
        }

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
        roleRepository.findById(PredefinedRole.CUSTOMER_ROLE)
                .ifPresent(roles::add);
        user.setRoles(roles);

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
}