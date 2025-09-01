package com.virtualpets.backend.service;

import com.virtualpets.backend.dto.request.PetRequest;
import com.virtualpets.backend.dto.response.PetResponse;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public interface PetService {

    PetResponse createPet(PetRequest petRequest, String username);

    List<PetResponse> getAllPets(String username, Collection<? extends GrantedAuthority> authorities);

    PetResponse getPetById(Long id, String username, Collection<? extends GrantedAuthority> authorities);

    PetResponse updatePet(Long id, PetRequest petRequest, String username, Collection<? extends GrantedAuthority> authorities);

    void deletePet(Long id, String username, Collection<? extends GrantedAuthority> authorities);

}