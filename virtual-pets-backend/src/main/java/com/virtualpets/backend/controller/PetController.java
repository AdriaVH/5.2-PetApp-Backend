package com.virtualpets.backend.controller;

import com.virtualpets.backend.dto.request.PetRequest;
import com.virtualpets.backend.dto.response.PetResponse;
import com.virtualpets.backend.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
@Tag(name = "Pets", description = "Endpoints for managing virtual pets")
public class PetController {

    private final PetService petService;

    @PostMapping
    @Operation(summary = "Create a new pet", description = "ROLE_USER: creates a pet linked to the authenticated user")
    public PetResponse create(@RequestBody PetRequest request, Authentication auth) {
        return petService.createPet(request, auth.getName());
    }

    @GetMapping
    @Operation(summary = "Get user pets", description = "ROLE_USER: fetches only the authenticated user's pets")
    public List<PetResponse> getUserPets(Authentication auth) {
        return petService.getUserPets(auth.getName());
    }

    @GetMapping("/all")
    @Operation(summary = "Get all pets", description = "ROLE_ADMIN: fetches all pets from all users")
    public List<PetResponse> getAllPets() {
        return petService.getAllPets();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a pet", description = "ROLE_USER: updates a pet owned by the authenticated user")
    public PetResponse update(@PathVariable Long id, @RequestBody PetRequest request, Authentication auth) {
        return petService.updatePet(id, request, auth.getName());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a pet", description = "ROLE_USER: deletes a pet owned by the authenticated user")
    public void delete(@PathVariable Long id, Authentication auth) {
        petService.deletePet(id, auth.getName());
    }
}
