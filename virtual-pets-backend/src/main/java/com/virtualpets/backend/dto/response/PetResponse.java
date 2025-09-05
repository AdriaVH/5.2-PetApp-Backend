package com.virtualpets.backend.dto.response;

import com.virtualpets.backend.model.Pet.PetType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object for a pet")
public record PetResponse(
        @Schema(description = "ID of the pet", example = "1")
        Long id,

        @Schema(description = "Name of the pet", example = "Sparky")
        String name,

        @Schema(description = "Type of the pet", example = "DOG")
        PetType type,

        @Schema(description = "Age of the pet in years", example = "3")
        Integer age,

        @Schema(description = "Username of the pet owner", example = "alice")
        String ownerUsername
) {}
