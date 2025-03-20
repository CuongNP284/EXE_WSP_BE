package com.wsp.workshophy.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wsp.workshophy.constant.PredefinedRole;
import com.wsp.workshophy.dto.request.UserCreationRequest;
import com.wsp.workshophy.dto.request.UserUpdateRequest;
import com.wsp.workshophy.dto.response.UserResponse;
import com.wsp.workshophy.entity.Role;
import com.wsp.workshophy.entity.User;
import com.wsp.workshophy.exception.AppException;
import com.wsp.workshophy.exception.ErrorCode;
import com.wsp.workshophy.mapper.UserMapper;
import com.wsp.workshophy.repository.RoleRepository;
import com.wsp.workshophy.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.CUSTOMER_ROLE).ifPresent(roles::add);

        user.setRoles(roles);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        return userMapper.toUserResponse(user);
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        log.info("Name: {}", name);

        User user = userRepository.findByEmailAndActive(name, true).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    @PostAuthorize("returnObject.username == authentication.name or hasRole('ADMIN')")
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findByIdAndActive(userId, true)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Update only if values are provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getFirstName() != null && !request.getFirstName().isEmpty()) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isEmpty()) {
            user.setLastName(request.getLastName());
        }
        if (request.getDob() != null) {
            user.setDob(request.getDob());
        }
        if (request.getStreet() != null && !request.getStreet().isEmpty()) {
            user.setStreet(request.getStreet());
        }
        if (request.getCity() != null && !request.getCity().isEmpty()) {
            user.setCity(request.getCity());
        }
        if (request.getDistrict() != null && !request.getDistrict().isEmpty()) {
            user.setDistrict(request.getDistrict());
        }
        if (request.getWard() != null && !request.getWard().isEmpty()) {
            user.setWard(request.getWard());
        }
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            var roles = roleRepository.findAllById(request.getRoles());
            user.setRoles(new HashSet<>(roles));
        }

        // Save and return updated user response
        return userMapper.toUserResponse(userRepository.save(user));
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId) {
        User user = userRepository.findByIdAndActive(userId, true)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setActive(false);
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        log.info("In method get Users");
        return userRepository.findAllByActive(true).stream().map(userMapper::toUserResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(
                userRepository.findByIdAndActive(id, true).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }
}
