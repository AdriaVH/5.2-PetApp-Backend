package com.virtualpets.virtual_pets_backend;

import com.virtualpets.backend.dto.request.PetRequest;
import com.virtualpets.backend.dto.response.PetResponse;
import com.virtualpets.backend.model.Pet;
import com.virtualpets.backend.model.Pet.PetType;
import com.virtualpets.backend.model.Role;
import com.virtualpets.backend.model.User;
import com.virtualpets.backend.repository.PetRepository;
import com.virtualpets.backend.repository.UserRepository;
import com.virtualpets.backend.service.impl.PetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceImplTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PetServiceImpl petService;

    private User user;
    private User adminUser;
    private Pet pet;
    private PetRequest petRequest;

    @BeforeEach
    void setup() {
        // Regular user
        user = User.builder()
                .id(1L)
                .username("bob")
                .password("pass")
                .roles(new HashSet<>())
                .build();

        // Admin user with both roles (Long id, String name)
        Role adminRole = new Role(1L, "ROLE_ADMIN");
        Role userRole = new Role(2L, "ROLE_USER");
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminRoles.add(userRole);

        adminUser = User.builder()
                .id(99L)
                .username("admin")
                .password("adminpass")
                .roles(adminRoles)
                .build();

        pet = Pet.builder()
                .id(1L)
                .name("Buddy")
                .type(PetType.DOG)
                .age(3)
                .owner(user)
                .build();

        petRequest = new PetRequest("Buddy", PetType.DOG, 3);
    }

    @Test
    void createPet_shouldReturnPetResponse() {
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));
        when(petRepository.save(any(Pet.class))).thenReturn(pet);

        PetResponse response = petService.createPet(petRequest, "bob");

        assertNotNull(response);
        assertEquals("Buddy", response.name());
        assertEquals("bob", response.ownerUsername());
    }

    @Test
    void getAllPets_shouldReturnPetsForUser() {
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));
        Page<Pet> petPage = new PageImpl<>(List.of(pet));
        when(petRepository.findByOwner(eq(user), any(PageRequest.class))).thenReturn(petPage);

        Page<PetResponse> pets = petService.getAllPets("bob", PageRequest.of(0, 10));

        assertEquals(1, pets.getTotalElements());
        assertEquals("Buddy", pets.getContent().get(0).name());
    }

    @Test
    void getAllPets_shouldReturnAllPetsForAdmin() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(
                User.builder().id(2L).username("admin").roles(Set.of(
                        new Role(1L, "ROLE_USER"),
                        new Role(2L, "ROLE_ADMIN")
                )).build()
        ));

        Page<Pet> petPage = new PageImpl<>(List.of(pet));
        when(petRepository.findAll(any(PageRequest.class))).thenReturn(petPage);

        Page<PetResponse> pets = petService.getAllPets("admin", PageRequest.of(0, 10));

        assertEquals(1, pets.getTotalElements());
        assertEquals("Buddy", pets.getContent().get(0).name());
    }

    @Test
    void getPetById_shouldReturnPetWhenOwner() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));

        PetResponse response = petService.getPetById(1L, "bob");

        assertEquals("Buddy", response.name());
    }

    @Test
    void getPetById_shouldThrowWhenNotOwnerOrAdmin() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(
                User.builder().id(2L).username("alice").roles(new HashSet<>()).build()
        ));

        Exception ex = assertThrows(RuntimeException.class, () ->
                petService.getPetById(1L, "alice"));

        assertTrue(ex.getMessage().contains("not authorized"));
    }

    @Test
    void updatePet_shouldUpdateAndReturnPet() {
        PetRequest updateRequest = new PetRequest("BuddyUpdated", PetType.DOG, 4);
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));
        when(petRepository.save(any(Pet.class))).thenReturn(pet);

        PetResponse updated = petService.updatePet(1L, updateRequest, "bob");

        assertEquals("BuddyUpdated", updated.name());
        assertEquals(4, updated.age());
    }

    @Test
    void deletePet_shouldCallRepository() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));

        petService.deletePet(1L, "bob");

        verify(petRepository).deleteById(1L);
    }
}
