package com.virtualpets.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object containing pet details")
public record PetResponse(
        @Schema(description = "Pet ID", example = "1") Long id,
        @Schema(description = "Name of the pet", example = "Bella") String name,
        @Schema(description = "Type of the pet", example = "Dragon") String type,
        @Schema(description = "Color of the pet", example = "Blue") String color,
        @Schema(description = "Username of the pet owner", example = "john_doe") String ownerUsername
) {}
