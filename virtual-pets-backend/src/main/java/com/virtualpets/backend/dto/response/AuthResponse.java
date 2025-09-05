package com.virtualpets.backend.dto.response;

import java.util.Set;

public record AuthResponse(
        String username,
        String token,
        Set<String> roles
) {}
