package com.virtualpets.backend.controller;

import com.virtualpets.backend.dto.request.LoginRequest;
import com.virtualpets.backend.dto.request.RegisterRequest;
import com.virtualpets.backend.dto.response.AuthResponse;
import com.virtualpets.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Registers a user with ROLE_USER")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Logs in a user and returns a JWT")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
