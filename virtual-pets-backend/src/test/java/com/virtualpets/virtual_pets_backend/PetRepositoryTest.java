package com.virtualpets.virtual_pets_backend;

import com.virtualpets.backend.VirtualPetsBackendApplication;
import com.virtualpets.backend.model.Pet;
import com.virtualpets.backend.model.User;
import com.virtualpets.backend.repository.PetRepository;
import com.virtualpets.backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ContextConfiguration(classes = VirtualPetsBackendApplication.class)
public class PetRepositoryTest {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private Pet pet;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .username("testuser")
                .password("password")
                .build();
        userRepository.save(owner);

        pet = Pet.builder()
                .name("Buddy")
                .type(Pet.PetType.DOG)
                .age(3)
                .owner(owner)
                .build();
        petRepository.save(pet);
    }

    @AfterEach
    void tearDown() {
        petRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void whenFindByOwnerWithPageable_thenReturnPetsPage() {
        Page<Pet> petsPage = petRepository.findByOwner(owner, PageRequest.of(0, 10));

        assertThat(petsPage).isNotNull();
        assertThat(petsPage.getTotalElements()).isEqualTo(1);
        assertThat(petsPage.getContent().getFirst().getOwner().getUsername()).isEqualTo("testuser");
    }

    @Test
    void whenDeletePet_thenPetShouldBeDeleted() {
        Long petId = pet.getId();
        petRepository.deleteById(petId);
        Optional<Pet> deletedPet = petRepository.findById(petId);
        assertThat(deletedPet).isNotPresent();
    }
}
