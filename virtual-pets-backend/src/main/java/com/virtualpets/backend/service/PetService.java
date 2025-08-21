package com.virtualpets.backend.service;

import com.virtualpets.backend.dto.request.PetRequest;
import com.virtualpets.backend.dto.response.PetResponse;
import java.util.List;

public interface PetService {
    PetResponse createPet(PetRequest request, String username);
    List<PetResponse> getUserPets(String username);
    List<PetResponse> getAllPets();
    PetResponse updatePet(Long id, PetRequest request, String username);
    void deletePet(Long id, String username);
}

