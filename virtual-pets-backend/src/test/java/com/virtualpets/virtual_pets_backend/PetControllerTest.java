package com.virtualpets.virtual_pets_backend;

import com.virtualpets.backend.VirtualPetsBackendApplication;
import com.virtualpets.backend.model.Pet;
import com.virtualpets.backend.model.User;
import com.virtualpets.backend.repository.PetRepository;
import com.virtualpets.backend.repository.UserRepository;
import com.virtualpets.backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = VirtualPetsBackendApplication.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    private String userToken;
    private String ownerToken;
    private Long petId;

    @BeforeAll
    void setupUsers() {
        // Clean DB
        petRepository.deleteAll();
        userRepository.deleteAll();

        // Create a normal user
        User user = new User();
        user.setUsername("bob");
        user.setPassword("password");
        userRepository.save(user);

        // Create an owner user
        User owner = new User();
        owner.setUsername("alice");
        owner.setPassword("password");
        owner = userRepository.save(owner);

        // Generate JWTs
        userToken = "Bearer " + jwtUtil.generateToken(user.getUsername(), Set.of("ROLE_USER"));
        ownerToken = "Bearer " + jwtUtil.generateToken(owner.getUsername(), Set.of("ROLE_USER"));
    }

    @BeforeEach
    void setupPet() {
        // Clean previous pets
        petRepository.deleteAll();

        // Re-create the pet before each test
        User owner = userRepository.findByUsername("alice").orElseThrow();
        Pet pet = new Pet();
        pet.setName("Buddy");
        pet.setAge(3);
        pet.setType("CAT");
        pet.setOwner(owner);
        pet = petRepository.save(pet);
        petId = pet.getId();
    }

    @Test
    void deletePet_unauthenticated_shouldReturn401() throws Exception {
        mockMvc.perform(delete("/pets/" + petId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deletePet_userNotOwner_shouldReturn403() throws Exception {
        mockMvc.perform(delete("/pets/" + petId)
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletePet_owner_shouldSucceed() throws Exception {
        mockMvc.perform(delete("/pets/" + petId)
                        .header("Authorization", ownerToken))
                .andExpect(status().isOk());
    }

    @Test
    void createPet_shouldSucceed() throws Exception {
        String newPetJson = """
            {
                "name": "Max",
                "age": 2,
                "type": "DOG"
            }
            """;

        mockMvc.perform(post("/pets")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newPetJson))
                .andExpect(status().isCreated()) // <-- changed from isOk() to isCreated()
                .andExpect(jsonPath("$.name").value("Max"))
                .andExpect(jsonPath("$.type").value("DOG"))
                .andExpect(jsonPath("$.age").value(2))
                .andExpect(jsonPath("$.ownerUsername").value("alice"));
    }

}
