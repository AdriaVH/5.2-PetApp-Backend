package com.virtualpets.virtual_pets_backend;

import com.virtualpets.backend.dto.request.PetRequest;
import com.virtualpets.backend.dto.response.PetResponse;
import com.virtualpets.backend.model.Pet;
import com.virtualpets.backend.model.Pet.PetType;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    private Pet pet;
    private PetRequest petRequest;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).username("bob").password("pass").build();
        pet = Pet.builder().id(1L).name("Buddy").type(PetType.DOG).age(3).owner(user).build();
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
        verify(userRepository).findByUsername("bob");
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    void getAllPets_shouldReturnPetsForUser() {
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));
        when(petRepository.findByOwner(user)).thenReturn(List.of(pet));

        Collection<SimpleGrantedAuthority> authorities = List.of();
        List<PetResponse> pets = petService.getAllPets("bob", authorities);

        assertEquals(1, pets.size());
        assertEquals("Buddy", pets.get(0).name());
        verify(userRepository).findByUsername("bob");
        verify(petRepository).findByOwner(user);
    }

    @Test
    void getAllPets_shouldReturnAllPetsForAdmin() {
        when(petRepository.findAll()).thenReturn(List.of(pet));

        Collection<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        List<PetResponse> pets = petService.getAllPets("bob", authorities);

        assertEquals(1, pets.size());
        assertEquals("Buddy", pets.get(0).name());
        verify(petRepository).findAll();
        verifyNoInteractions(userRepository);
    }

    @Test
    void getPetById_shouldReturnPetWhenOwner() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        Collection<SimpleGrantedAuthority> authorities = List.of();
        PetResponse response = petService.getPetById(1L, "bob", authorities);
        assertEquals("Buddy", response.name());
    }

    @Test
    void getPetById_shouldThrowWhenNotOwnerOrAdmin() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        Collection<SimpleGrantedAuthority> authorities = List.of();
        Exception ex = assertThrows(RuntimeException.class, () ->
                petService.getPetById(1L, "alice", authorities));
        assertTrue(ex.getMessage().contains("not authorized"));
    }

    @Test
    void updatePet_shouldUpdateAndReturnPet() {
        PetRequest updateRequest = new PetRequest("BuddyUpdated", PetType.DOG, 4);
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenReturn(pet);

        Collection<SimpleGrantedAuthority> authorities = List.of();
        PetResponse updated = petService.updatePet(1L, updateRequest, "bob", authorities);

        assertEquals("BuddyUpdated", updated.name());
        assertEquals(4, updated.age());
    }

    @Test
    void deletePet_shouldCallRepository() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        Collection<SimpleGrantedAuthority> authorities = List.of();
        petService.deletePet(1L, "bob", authorities);
        verify(petRepository).deleteById(1L);
    }
}
