package com.virtualpets.virtual_pets_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virtualpets.backend.VirtualPetsBackendApplication;
import com.virtualpets.backend.controller.PetController;
import com.virtualpets.backend.dto.request.PetRequest;
import com.virtualpets.backend.dto.response.PetResponse;
import com.virtualpets.backend.dto.response.ErrorResponse;
import com.virtualpets.backend.model.Pet.PetType;
import com.virtualpets.backend.service.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PetController.class)
@ContextConfiguration(classes = VirtualPetsBackendApplication.class)
public class PetControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PetService petService;

    private PetResponse petResponse;
    private PetRequest petRequest;

    @BeforeEach
    void setUp() {
        petResponse = new PetResponse(1L, "Buddy", PetType.DOG, 3, "bob");
        petRequest = new PetRequest("New Name", PetType.CAT, 4);
    }

    @Test
    @WithMockUser
    void createPet_shouldReturnCreated_whenValidRequest() throws Exception {
        when(petService.createPet(any(PetRequest.class), anyString())).thenReturn(petResponse);

        mockMvc.perform(post("/pets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Buddy"))
                .andExpect(jsonPath("$.type").value("DOG"))
                .andExpect(jsonPath("$.ownerUsername").value("bob"));
    }

    @Test
    @WithMockUser(username = "bob", roles = "USER")
    void getPets_shouldReturnListOfPets_whenAuthenticated() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PetResponse> page = new PageImpl<>(List.of(petResponse));

        // Mock the new method signature with Pageable
        when(petService.getAllPets(anyString(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/pets")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Buddy"))
                .andExpect(jsonPath("$.content[0].type").value("DOG"))
                .andExpect(jsonPath("$.content[0].ownerUsername").value("bob"));
    }

    @Test
    @WithMockUser
    void createPet_withInvalidType_shouldReturnValidationError() throws Exception {
        PetRequest invalidRequest = new PetRequest("Invalid", null, 2); // PetType null

        String response = mockMvc.perform(post("/pets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertThat(errorResponse.details()).containsKey("type");
    }
}
