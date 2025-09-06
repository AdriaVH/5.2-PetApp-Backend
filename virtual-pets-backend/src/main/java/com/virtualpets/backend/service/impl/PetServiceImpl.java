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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    public PetServiceImpl(PetRepository petRepository, UserRepository userRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public PetResponse createPet(PetRequest petRequest, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        Pet pet = Pet.builder()
                .name(petRequest.name())
                .type(petRequest.type())
                .age(petRequest.age())
                .owner(user)
                .build();

        return PetMapper.toResponse(petRepository.save(pet));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetResponse> getAllPets(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        if (user.hasRole("ROLE_ADMIN")) {
            return petRepository.findAll(pageable)
                    .map(PetMapper::toResponse);
        }

        return petRepository.findByOwner(user, pageable)
                .map(PetMapper::toResponse);
    }


    @Override
    @Transactional(readOnly = true)
    public PetResponse getPetById(Long id, String username) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        if (pet.canBeManagedBy(user)) {
            throw new UnauthorizedActionException("You are not authorized to view this pet");
        }

        return PetMapper.toResponse(pet);
    }

    @Override
    @Transactional
    public PetResponse updatePet(Long id, PetRequest petRequest, String username) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        if (pet.canBeManagedBy(user)) {
            throw new UnauthorizedActionException("You are not authorized to update this pet");
        }

        pet.setName(petRequest.name());
        pet.setAge(petRequest.age());
        pet.setType(petRequest.type());

        return PetMapper.toResponse(petRepository.save(pet));
    }

    @Override
    @Transactional
    public void deletePet(Long id, String username) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        if (pet.canBeManagedBy(user)) {
            throw new UnauthorizedActionException("You are not authorized to delete this pet");
        }

        petRepository.deleteById(id);
    }
}
