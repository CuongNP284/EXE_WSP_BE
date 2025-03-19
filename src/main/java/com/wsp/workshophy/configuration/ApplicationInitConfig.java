package com.wsp.workshophy.configuration;

import java.util.HashSet;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.wsp.workshophy.constant.PredefinedRole;
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
    static final String ADMIN_PASSWORD = "admin";

    @NonFinal
    static final String CUSTOMER_PASSWORD = "cutomer";

    @NonFinal
    static final String ORGANIZER_PASSWORD = "organizer";

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        log.info("Initializing application.....");
        return args -> {
            if (userRepository.findByUsername(ADMIN_USER_NAME).isEmpty()) {
                Role adminRole = roleRepository.save(Role.builder()
                        .name(PredefinedRole.ADMIN_ROLE)
                        .description("Admin role")
                        .build());
                var roles = new HashSet<Role>();
                roles.add(adminRole);
                User admin = User.builder()
                        .username(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .lastName("admin")
                        .firstName("admin")
                        .roles(roles)
                        .build();
                userRepository.save(admin);
                log.warn("admin user has been created with default password: admin, please change it");
            }
            if (userRepository.findByUsername(CUSTOMER_USER_NAME).isEmpty()) {
                Role customerRole = roleRepository.save(Role.builder()
                        .name(PredefinedRole.CUSTOMER_ROLE)
                        .description("Customer role")
                        .build());
                var roles = new HashSet<Role>();
                roles.add(customerRole);
                User customer = User.builder()
                        .username(CUSTOMER_USER_NAME)
                        .password(passwordEncoder.encode(CUSTOMER_PASSWORD))
                        .lastName("customer")
                        .firstName("customer")
                        .roles(roles)
                        .build();
                userRepository.save(customer);
                log.warn("customer user has been created with default password: cutomer, please change it");
            }
            if (userRepository.findByUsername(ORGANIZER_USER_NAME).isEmpty()) {
                Role organizerRole = roleRepository.save(Role.builder()
                        .name(PredefinedRole.ORGANIZER_ROLE)
                        .description("Organizer role")
                        .build());
                var roles = new HashSet<Role>();
                roles.add(organizerRole);
                User organizer = User.builder()
                        .username(ORGANIZER_USER_NAME)
                        .password(passwordEncoder.encode(ORGANIZER_PASSWORD))
                        .lastName("organizer")
                        .firstName("organizer")
                        .roles(roles)
                        .build();
                userRepository.save(organizer);
                log.warn("organizer user has been created with default password: organizer, please change it");
            }
            log.info("Application initialization completed .....");
        };
    }
}
