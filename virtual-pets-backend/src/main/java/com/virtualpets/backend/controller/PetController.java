package com.virtualpets.backend.controller;

import com.virtualpets.backend.dto.request.PetRequest;
import com.virtualpets.backend.dto.response.PetResponse;
import com.virtualpets.backend.service.PetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    public ResponseEntity<PetResponse> createPet(@Valid @RequestBody PetRequest petRequest, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(petService.createPet(petRequest, authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<List<PetResponse>> getAllPets(Authentication authentication) {
        // Authorization is handled inside the service method
        return ResponseEntity.ok(petService.getAllPets(authentication.getName(), authentication.getAuthorities()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetResponse> getPetById(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(petService.getPetById(id, authentication.getName(), authentication.getAuthorities()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetResponse> updatePet(@PathVariable Long id, @Valid @RequestBody PetRequest petRequest, Authentication authentication) {
        // Authorization is handled inside the service method
        return ResponseEntity.ok(petService.updatePet(id, petRequest, authentication.getName(), authentication.getAuthorities()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Long id, Authentication authentication) {
        // Authorization is handled inside the service method
        petService.deletePet(id, authentication.getName(), authentication.getAuthorities());
        return ResponseEntity.ok().build();
    }
}