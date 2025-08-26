package com.virtualpets.backend.service.impl;

import com.virtualpets.backend.dto.request.LoginRequest;
import com.virtualpets.backend.dto.request.RegisterRequest;
import com.virtualpets.backend.dto.response.AuthResponse;
import com.virtualpets.backend.exception.InvalidCredentialsException;
import com.virtualpets.backend.exception.UserAlreadyExistsException;
import com.virtualpets.backend.model.Role;
import com.virtualpets.backend.model.User;
import com.virtualpets.backend.repository.RoleRepository;
import com.virtualpets.backend.repository.UserRepository;
import com.virtualpets.backend.service.AuthService;
import com.virtualpets.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);

        return new AuthResponse(
                jwtUtil.generateToken(user.getUsername(), user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())),
                user.getUsername()
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        return new AuthResponse(
                jwtUtil.generateToken(user.getUsername(), user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())),
                user.getUsername()
        );
    }
}
