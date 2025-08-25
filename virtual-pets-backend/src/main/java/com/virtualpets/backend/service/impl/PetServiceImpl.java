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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    @Override
    public PetResponse createPet(PetRequest request, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Pet pet = Pet.builder()
                .name(request.name())
                .type(request.type())
                .age(request.age())
                .owner(owner)
                .build();

        return PetMapper.toResponse(petRepository.save(pet));
    }

    @Override
    public List<PetResponse> getUserPets(String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return petRepository.findByOwner(owner)
                .stream()
                .map(PetMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PetResponse> getAllPets() {
        return petRepository.findAll()
                .stream()
                .map(PetMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PetResponse updatePet(Long id, PetRequest request, String username) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found"));

        if (!pet.getOwner().getUsername().equals(username)) {
            throw new UnauthorizedActionException("You are not the owner of this pet");
        }

        pet.setName(request.name());
        pet.setType(request.type());
        pet.setAge(request.age());

        return PetMapper.toResponse(petRepository.save(pet));
    }

    @Override
    public void deletePet(Long id, String username) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found"));

        if (!pet.getOwner().getUsername().equals(username)) {
            throw new UnauthorizedActionException("You are not the owner of this pet");
        }

        petRepository.delete(pet);
    }
}
