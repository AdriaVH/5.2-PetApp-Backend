package com.virtualpets.backend.controller;

import com.virtualpets.backend.dto.request.PetRequest;
import com.virtualpets.backend.dto.response.PetResponse;
import com.virtualpets.backend.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/pets")
@SecurityRequirement(name = "bearerAuth")  // <-- add this
@RequiredArgsConstructor
@Tag(name = "Pets", description = "Endpoints for managing virtual pets")
public class PetController {

    private final PetService petService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new pet", description = "ROLE_USER: creates a pet linked to the authenticated user")
    public PetResponse create(@RequestBody PetRequest request, Authentication auth) {
        return petService.createPet(request, auth.getName());
    }

    @GetMapping
    @Operation(summary = "Get pets for the user", description = "ROLE_USER: own pets, ROLE_ADMIN: all pets")
    public List<PetResponse> getPets(Authentication auth) {
        Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
        return petService.getPets(auth.getName(), roles);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all pets", description = "ROLE_ADMIN only: fetch all pets")
    public List<PetResponse> getAllPets() {
        return petService.getAllPets();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a pet", description = "ROLE_USER can update own pets, ROLE_ADMIN can update any pet")
    public PetResponse update(@PathVariable Long id, @RequestBody PetRequest request, Authentication auth) {
        Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
        return petService.updatePet(id, request, auth.getName(), roles);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a pet", description = "ROLE_USER can delete own pets, ROLE_ADMIN can delete any pet")
    public void delete(@PathVariable Long id, Authentication auth) {
        Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
        petService.deletePet(id, auth.getName(), roles);
    }
}
