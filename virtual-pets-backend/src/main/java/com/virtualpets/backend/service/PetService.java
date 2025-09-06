package com.virtualpets.backend.service;

import com.virtualpets.backend.dto.request.PetRequest;
import com.virtualpets.backend.dto.response.PetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PetService {
    PetResponse createPet(PetRequest petRequest, String username);

    Page<PetResponse> getAllPets(String username, Pageable pageable);

    PetResponse getPetById(Long id, String username);

    PetResponse updatePet(Long id, PetRequest petRequest, String username);

    void deletePet(Long id, String username);
}
