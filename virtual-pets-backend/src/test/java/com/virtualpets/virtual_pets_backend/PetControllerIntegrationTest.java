package com.virtualpets.virtual_pets_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virtualpets.backend.VirtualPetsBackendApplication;
import com.virtualpets.backend.dto.request.PetRequest;
import com.virtualpets.backend.model.Pet;
import com.virtualpets.backend.model.Role;
import com.virtualpets.backend.model.User;
import com.virtualpets.backend.repository.PetRepository;
import com.virtualpets.backend.repository.RoleRepository;
import com.virtualpets.backend.repository.UserRepository;
import com.virtualpets.backend.util.JwtUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = VirtualPetsBackendApplication.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String userToken;
    private String anotherUserToken;
    private String adminToken;
    private Long petId;

    @BeforeAll
    void setupRolesAndUsers() {
        petRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role userRole = roleRepository.save(new Role(null, "ROLE_USER"));
        Role adminRole = roleRepository.save(new Role(null, "ROLE_ADMIN"));

        User user = new User();
        user.setUsername("bob");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);

        User anotherUser = new User();
        anotherUser.setUsername("alice");
        anotherUser.setPassword(passwordEncoder.encode("password"));
        anotherUser.setRoles(Set.of(userRole));
        userRepository.save(anotherUser);

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setRoles(Set.of(userRole, adminRole));
        userRepository.save(admin);

        userToken = "Bearer " + jwtUtil.generateToken(user.getUsername(), Set.of(userRole.getName()));
        anotherUserToken = "Bearer " + jwtUtil.generateToken(anotherUser.getUsername(), Set.of(userRole.getName()));
        adminToken = "Bearer " + jwtUtil.generateToken(admin.getUsername(), Set.of(userRole.getName(), adminRole.getName()));
    }

    @BeforeEach
    void setupPet() {
        petRepository.deleteAll();
        User owner = userRepository.findByUsername("bob").orElseThrow();
        Pet pet = new Pet();
        pet.setName("Buddy");
        pet.setAge(3);
        pet.setType("DOG");
        pet.setOwner(owner);
        pet = petRepository.save(pet);
        petId = pet.getId();
    }

    @Test
    void createPet_shouldSucceed_whenUserAuthenticated() throws Exception {
        PetRequest newPet = new PetRequest("Max", "CAT", 2);
        mockMvc.perform(post("/pets")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPet)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Max"))
                .andExpect(jsonPath("$.ownerUsername").value("bob"));
    }

    @Test
    void getPets_shouldReturnUsersOwnPets_whenUserAuthenticated() throws Exception {
        mockMvc.perform(get("/pets")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Buddy"));
    }

    @Test
    void getPets_shouldReturnAllPets_whenAdminAuthenticated() throws Exception {
        User anotherUser = userRepository.findByUsername("alice").orElseThrow();
        Pet anotherPet = new Pet();
        anotherPet.setName("Fluffy");
        anotherPet.setAge(5);
        anotherPet.setType("BIRD");
        anotherPet.setOwner(anotherUser);
        petRepository.save(anotherPet);

        mockMvc.perform(get("/pets")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updatePet_shouldSucceed_whenOwnerAuthenticated() throws Exception {
        PetRequest updatedPet = new PetRequest("New Name", "New Type", 4);
        mockMvc.perform(put("/pets/" + petId)
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void updatePet_shouldSucceed_whenAdminAuthenticated() throws Exception {
        PetRequest updatedPet = new PetRequest("Admin Update", "Admin Type", 6);
        mockMvc.perform(put("/pets/" + petId)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Admin Update"));
    }

    @Test
    void updatePet_shouldReturn403_whenNotOwner() throws Exception {
        PetRequest updatedPet = new PetRequest("Unauthorized", "Unauthorized", 1);
        mockMvc.perform(put("/pets/" + petId)
                        .header("Authorization", anotherUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPet)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletePet_shouldSucceed_whenOwnerAuthenticated() throws Exception {
        mockMvc.perform(delete("/pets/" + petId)
                        .header("Authorization", userToken))
                .andExpect(status().isOk());
    }

    @Test
    void deletePet_shouldSucceed_whenAdminAuthenticated() throws Exception {
        mockMvc.perform(delete("/pets/" + petId)
                        .header("Authorization", adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void deletePet_shouldReturn403_whenNotOwner() throws Exception {
        mockMvc.perform(delete("/pets/" + petId)
                        .header("Authorization", anotherUserToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletePet_shouldReturn404_whenPetNotFound() throws Exception {
        mockMvc.perform(delete("/pets/999")
                        .header("Authorization", userToken))
                .andExpect(status().isNotFound());
    }
}