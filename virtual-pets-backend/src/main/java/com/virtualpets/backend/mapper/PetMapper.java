package com.virtualpets.backend.mapper;

import com.virtualpets.backend.dto.request.PetRequest;
import com.virtualpets.backend.dto.response.PetResponse;
import com.virtualpets.backend.model.Pet;

public class PetMapper {
    public static Pet toEntity(PetRequest dto) {
        return Pet.builder()
                .name(dto.name())
                .type(dto.type())
                .color(dto.color())
                .build();
    }

    public static PetResponse toResponse(Pet entity) {
        return new PetResponse(
                entity.getId(),
                entity.getName(),
                entity.getType(),
                entity.getColor(),
                entity.getOwner().getUsername()
        );
    }
}
