package com.virtualpets.backend.mapper;

import com.virtualpets.backend.dto.response.PetResponse;
import com.virtualpets.backend.model.Pet;

public class PetMapper {

    public static PetResponse toResponse(Pet pet) {
        return new PetResponse(
                pet.getId(),
                pet.getName(),
                pet.getType(),  // updated to enum
                pet.getAge(),
                pet.getOwner().getUsername()
        );
    }
}
