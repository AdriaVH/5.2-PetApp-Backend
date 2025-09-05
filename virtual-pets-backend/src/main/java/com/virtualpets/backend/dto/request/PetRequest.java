package com.virtualpets.backend.dto.request;

import com.virtualpets.backend.model.Pet.PetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request DTO for creating or updating a pet")
public record PetRequest(

        @Schema(description = "Name of the pet", example = "Buddy")
        @NotBlank(message = "Name is mandatory")
        String name,

        @Schema(description = "Type of the pet", example = "DOG")
        @NotNull(message = "Pet type is mandatory")
        PetType type,

        @Schema(description = "Age of the pet", example = "3")
        @Min(value = 0, message = "Age must be at least 0")
        int age
) { }
