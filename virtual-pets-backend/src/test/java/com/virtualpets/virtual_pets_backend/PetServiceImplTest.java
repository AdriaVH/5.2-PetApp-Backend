package com.virtualpets.virtual_pets_backend;

import com.virtualpets.backend.dto.request.PetRequest;
import com.virtualpets.backend.dto.response.PetResponse;
import com.virtualpets.backend.exception.UnauthorizedActionException;
import com.virtualpets.backend.model.Pet;
import com.virtualpets.backend.model.User;
import com.virtualpets.backend.repository.PetRepository;
import com.virtualpets.backend.repository.UserRepository;
import com.virtualpets.backend.service.impl.PetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PetServiceImplTest {

    @InjectMocks
    private PetServiceImpl petService;

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserRepository userRepository;

    private User user;
    private Pet pet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .username("alice")
                .roles(Set.of())
                .build();

        pet = Pet.builder()
                .id(1L)
                .name("Buddy")
                .type("Dog")
                .age(3)
                .owner(user)
                .build();
    }

    @Test
    void createPet_success() {
        PetRequest request = new PetRequest("Buddy", "Dog", 3);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(petRepository.save(any(Pet.class))).thenReturn(pet);

        PetResponse response = petService.createPet(request, "alice");

        assertNotNull(response);
        assertEquals("Buddy", response.name());
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    void getPets_userPets_success() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(petRepository.findByOwner(user)).thenReturn(List.of(pet));

        List<PetResponse> pets = petService.getPets("alice", Set.of()); // empty roles = non-admin

        assertEquals(1, pets.size());
        assertEquals("Buddy", pets.getFirst().name());
    }

    @Test
    void updatePet_unauthorized_throws() {
        PetRequest request = new PetRequest("Buddy", "Dog", 3);
        User anotherUser = User.builder().username("bob").build();
        pet.setOwner(anotherUser);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        assertThrows(UnauthorizedActionException.class, () ->
                petService.updatePet(1L, request, "alice", Set.of())
        );
    }

    @Test
    void deletePet_success() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        petService.deletePet(1L, "alice", Set.of());

        verify(petRepository, times(1)).delete(pet);
    }
}
