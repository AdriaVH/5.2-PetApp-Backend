package com.virtualpets.backend.dto.request;

import com.virtualpets.backend.model.Pet.PetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request object for creating or updating a pet")
public record PetRequest(

        @NotNull(message = "Name is required")
        @Schema(description = "Name of the pet", example = "Sparky", required = true)
        String name,

        @NotNull(message = "Type is required")
        @Schema(description = "Type of the pet", example = "DOG", required = true)
        PetType type,

        @NotNull(message = "Age is required")
        @Min(value = 0, message = "Age must be 0 or greater")
        @Schema(description = "Age of the pet in years", example = "3", required = true)
        Integer age
) {}
