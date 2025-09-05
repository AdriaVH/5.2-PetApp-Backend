package com.virtualpets.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request DTO for user login")
public record LoginRequest(

        @Schema(description = "Username of the user", example = "alice")
        @NotBlank(message = "Username is mandatory")
        String username,

        @Schema(description = "Password of the user", example = "pass123")
        @NotBlank(message = "Password is mandatory")
        String password
) { }
