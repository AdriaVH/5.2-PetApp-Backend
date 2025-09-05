package com.virtualpets.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request DTO for registering a new user")
public record RegisterRequest(

        @Schema(description = "Username of the user", example = "alice")
        @NotBlank(message = "Username is mandatory")
        @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
        String username,

        @Schema(description = "Password of the user", example = "pass123")
        @NotBlank(message = "Password is mandatory")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password
) { }
