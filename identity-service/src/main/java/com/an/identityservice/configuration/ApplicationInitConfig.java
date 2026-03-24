package com.an.identityservice.configuration;

import java.util.HashSet;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.an.identityservice.constant.PredefinedRole;
import com.an.identityservice.entity.Role;
import com.an.identityservice.entity.User;
import com.an.identityservice.repository.RoleRepository;
import com.an.identityservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Configuration // anotation này để đánh dấu đây là một class cấu hình, Spring sẽ tự động phát hiện và xử lý nó khi khởi
// động ứng dụng
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @NonFinal
    static final String ADMIN_USER_NAME = "admin";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    @ConditionalOnProperty(
            prefix = "spring.datasource",
            name = "driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        log.info("Initializing application.....");
        return args -> {
            ensureRoleExists(roleRepository, PredefinedRole.USER_ROLE, "User role");
            Role adminRole = ensureRoleExists(roleRepository, PredefinedRole.ADMIN_ROLE, "Admin role");

            var adminUserOptional = userRepository.findByUsernameWithRoles(ADMIN_USER_NAME);
            if (adminUserOptional.isEmpty()) {
                var roles = new HashSet<Role>();
                roles.add(adminRole);

                User user = User.builder()
                        .username(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .roles(roles)
                        .build();

                userRepository.save(user);
                log.warn("admin user has been created with default password: admin, please change it");
            } else {
                log.info("Admin user already exists, skipping creation");
            }
            log.info("Application initialization completed .....");
        };
    }

    private Role ensureRoleExists(RoleRepository roleRepository, String roleName, String description) {
        return roleRepository
                .findById(roleName)
                .orElseGet(() -> roleRepository.save(
                        Role.builder().name(roleName).description(description).build()));
    }
}
