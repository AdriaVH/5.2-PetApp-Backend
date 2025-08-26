package com.virtualpets.virtual_pets_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virtualpets.backend.VirtualPetsBackendApplication;
import com.virtualpets.backend.dto.request.LoginRequest;
import com.virtualpets.backend.dto.request.RegisterRequest;
import com.virtualpets.backend.repository.PetRepository;
import com.virtualpets.backend.repository.UserRepository;
import com.virtualpets.backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = VirtualPetsBackendApplication.class)
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    @BeforeEach
    void setUp() {
        petRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void register_and_login_success() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("alice", "pass");

        // Register endpoint
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated()) // <-- expect 201
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.token").exists());

        // Login endpoint
        LoginRequest loginRequest = new LoginRequest("alice", "pass");
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.token").exists());
    }
}
