package com.virtualpets.backend.util;

import com.virtualpets.backend.model.Role;
import com.virtualpets.backend.model.User;
import com.virtualpets.backend.repository.RoleRepository;
import com.virtualpets.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData() {
        return args -> {
            // Create roles if they don’t exist
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_ADMIN")));
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_USER")));

            // Create or update admin user
            User admin = userRepository.findByUsername("admin").orElseGet(() -> {
                User newAdmin = new User();
                newAdmin.setUsername("admin");
                newAdmin.setPassword(passwordEncoder.encode("admin123")); // default password
                return newAdmin;
            });

            // Always ensure admin has both roles
            admin.setRoles(Set.of(adminRole, userRole));
            userRepository.save(admin);
        };
    }
}
