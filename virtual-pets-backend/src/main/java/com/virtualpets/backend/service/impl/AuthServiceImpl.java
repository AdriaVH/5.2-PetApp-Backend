package com.virtualpets.backend.service.impl;

import com.virtualpets.backend.dto.request.LoginRequest;
import com.virtualpets.backend.dto.request.RegisterRequest;
import com.virtualpets.backend.dto.response.AuthResponse;
import com.virtualpets.backend.model.User;
import com.virtualpets.backend.repository.UserRepository;
import com.virtualpets.backend.service.AuthService;
import com.virtualpets.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of("ROLE_USER"))
                .build();
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRoles());
        return new AuthResponse(token, user.getUsername());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRoles());
        return new AuthResponse(token, user.getUsername());
    }
}
