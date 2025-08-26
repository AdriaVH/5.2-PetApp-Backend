package com.virtualpets.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "User information returned to clients")
public record UserResponse(
        @Schema(description = "User ID", example = "1")
        Long id,

        @Schema(description = "Username", example = "alice")
        String username,

        @Schema(description = "Roles assigned to the user")
        Set<String> roles
) {}
