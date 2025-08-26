package com.virtualpets.backend.service;

import com.virtualpets.backend.dto.request.PetRequest;
import com.virtualpets.backend.dto.response.PetResponse;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public interface PetService {

    PetResponse createPet(PetRequest request, String username);

    List<PetResponse> getPets(String username, Collection<? extends GrantedAuthority> roles);

    List<PetResponse> getAllPets();

    PetResponse updatePet(Long id, PetRequest request, String username, Collection<? extends GrantedAuthority> roles);

    void deletePet(Long id, String username, Collection<? extends GrantedAuthority> roles);
}
