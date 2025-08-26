package com.virtualpets.backend.service.impl;

import com.virtualpets.backend.dto.request.PetRequest;
import com.virtualpets.backend.dto.response.PetResponse;
import com.virtualpets.backend.exception.ResourceNotFoundException;
import com.virtualpets.backend.exception.UnauthorizedActionException;
import com.virtualpets.backend.mapper.PetMapper;
import com.virtualpets.backend.model.Pet;
import com.virtualpets.backend.model.User;
import com.virtualpets.backend.repository.PetRepository;
import com.virtualpets.backend.repository.UserRepository;
import com.virtualpets.backend.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    @Override
    @CacheEvict(value = {"allPets", "userPets"}, allEntries = true)
    public PetResponse createPet(PetRequest request, String username) {
        User owner = getOwner(username);

        Pet pet = Pet.builder()
                .name(request.name())
                .type(request.type())
                .age(request.age())
                .owner(owner)
                .build();

        return PetMapper.toResponse(petRepository.save(pet));
    }

    @Override
    @Cacheable(value = "userPets", key = "#username")
    public List<PetResponse> getPets(String username, Collection<? extends GrantedAuthority> roles) {
        if (isAdmin(roles)) {
            return getAllPets();
        }

        User owner = getOwner(username);
        return petRepository.findByOwner(owner).stream()
                .map(PetMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "allPets")
    public List<PetResponse> getAllPets() {
        return petRepository.findAll().stream()
                .map(PetMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = {"allPets", "userPets"}, allEntries = true)
    public PetResponse updatePet(Long id, PetRequest request, String username, Collection<? extends GrantedAuthority> roles) {
        Pet pet = getPetById(id);
        checkAccess(pet, username, roles);

        pet.setName(request.name());
        pet.setType(request.type());
        pet.setAge(request.age());

        return PetMapper.toResponse(petRepository.save(pet));
    }

    @Override
    @CacheEvict(value = {"allPets", "userPets"}, allEntries = true)
    public void deletePet(Long id, String username, Collection<? extends GrantedAuthority> roles) {
        Pet pet = getPetById(id);
        checkAccess(pet, username, roles);
        petRepository.delete(pet);
    }

    // --- Helpers ---
    private boolean isAdmin(Collection<? extends GrantedAuthority> roles) {
        return roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
    }

    private void checkAccess(Pet pet, String username, Collection<? extends GrantedAuthority> roles) {
        if (!isAdmin(roles) && !pet.getOwner().getUsername().equals(username)) {
            throw new UnauthorizedActionException("You are not allowed to perform this action");
        }
    }

    private User getOwner(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Pet getPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found"));
    }
}
