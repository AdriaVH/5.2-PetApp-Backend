package com.virtualpets.backend.service;

import com.virtualpets.backend.dto.request.LoginRequest;
import com.virtualpets.backend.dto.request.RegisterRequest;
import com.virtualpets.backend.dto.response.AuthResponse;

public interface

AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
