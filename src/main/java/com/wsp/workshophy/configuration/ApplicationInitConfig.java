package com.wsp.workshophy.configuration;

import java.util.HashSet;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.wsp.workshophy.constant.PredefinedRole;
import com.wsp.workshophy.entity.Address; // Thêm import cho Address
import com.wsp.workshophy.entity.Role;
import com.wsp.workshophy.entity.User;
import com.wsp.workshophy.repository.RoleRepository;
import com.wsp.workshophy.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @NonFinal
    static final String ADMIN_USER_NAME = "admin";

    @NonFinal
    static final String CUSTOMER_USER_NAME = "customer";

    @NonFinal
    static final String ORGANIZER_USER_NAME = "organizer";

    @NonFinal
    static final String ADMIN_PASSWORD = "123456789";

    @NonFinal
    static final String CUSTOMER_PASSWORD = "123456789";

    @NonFinal
    static final String ORGANIZER_PASSWORD = "123456789";

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        log.info("Initializing application.....");
        return args -> {
            // Khởi tạo Admin
            if (userRepository.findByUsernameAndActive(ADMIN_USER_NAME, true).isEmpty()) {
                Role adminRole = roleRepository.save(Role.builder()
                        .name(PredefinedRole.ADMIN_ROLE)
                        .description("Admin role")
                        .build());
                var roles = new HashSet<Role>();
                roles.add(adminRole);

                // Tạo Address cho Admin
                Address adminAddress = Address.builder()
                        .street("123 Admin Street")
                        .city("Admin City")
                        .district("Admin District")
                        .ward("Admin Ward")
                        .build();

                User admin = User.builder()
                        .username(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .lastName("admin")
                        .email("admin@gmail.com")
                        .dob(java.time.LocalDate.parse("2004-08-02"))
                        .firstName("admin")
                        .roles(roles)
                        .address(adminAddress) // Gắn Address vào User
                        .build();

                userRepository.save(admin);
                log.warn("admin user has been created with default password: admin, please change it");
            }

            // Khởi tạo Customer
            if (userRepository.findByUsernameAndActive(CUSTOMER_USER_NAME, true).isEmpty()) {
                Role customerRole = roleRepository.save(Role.builder()
                        .name(PredefinedRole.CUSTOMER_ROLE)
                        .description("Customer role")
                        .build());
                var roles = new HashSet<Role>();
                roles.add(customerRole);

                // Tạo Address cho Customer
                Address customerAddress = Address.builder()
                        .street("456 Customer Street")
                        .city("Customer City")
                        .district("Customer District")
                        .ward("Customer Ward")
                        .build();

                User customer = User.builder()
                        .username(CUSTOMER_USER_NAME)
                        .password(passwordEncoder.encode(CUSTOMER_PASSWORD))
                        .lastName("customer")
                        .firstName("customer")
                        .email("customer@gmail.com")
                        .dob(java.time.LocalDate.parse("2004-08-02"))
                        .roles(roles)
                        .address(customerAddress) // Gắn Address vào User
                        .build();

                userRepository.save(customer);
                log.warn("customer user has been created with default password: customer, please change it");
            }

            // Khởi tạo Organizer
            if (userRepository.findByUsernameAndActive(ORGANIZER_USER_NAME, true).isEmpty()) {
                Role organizerRole = roleRepository.save(Role.builder()
                        .name(PredefinedRole.ORGANIZER_ROLE)
                        .description("Organizer role")
                        .build());
                var roles = new HashSet<Role>();
                roles.add(organizerRole);

                // Tạo Address cho Organizer
                Address organizerAddress = Address.builder()
                        .street("789 Organizer Street")
                        .city("Organizer City")
                        .district("Organizer District")
                        .ward("Organizer Ward")
                        .build();

                User organizer = User.builder()
                        .username(ORGANIZER_USER_NAME)
                        .password(passwordEncoder.encode(ORGANIZER_PASSWORD))
                        .lastName("organizer")
                        .firstName("organizer")
                        .email("organizer@gmail.com")
                        .dob(java.time.LocalDate.parse("2004-08-02"))
                        .roles(roles)
                        .address(organizerAddress) // Gắn Address vào User
                        .build();

                userRepository.save(organizer);
                log.warn("organizer user has been created with default password: organizer, please change it");
            }

            log.info("Application initialization completed .....");
        };
    }
}