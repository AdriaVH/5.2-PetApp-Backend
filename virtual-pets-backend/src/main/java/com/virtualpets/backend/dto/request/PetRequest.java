package com.virtualpets.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for creating or updating a pet")
public record PetRequest(
        @Schema(description = "Name of the pet", example = "Sparky") String name,
        @Schema(description = "Type of the pet", example = "Unicorn") String type,
        @Schema(description = "Color of the pet", example = "White") String color
) {}
