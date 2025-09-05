package com.virtualpets.backend.controller;

import com.virtualpets.backend.dto.request.PetRequest;
import com.virtualpets.backend.dto.response.PetResponse;
import com.virtualpets.backend.service.PetService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public PetResponse createPet(
            @Valid @RequestBody PetRequest petRequest,
            Authentication authentication) {
        String username = authentication.getName();
        return petService.createPet(petRequest, username);
    }

    @GetMapping
    public Page<PetResponse> getAllPets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Authentication authentication) {

        String username = authentication.getName();
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return petService.getAllPets(username, pageable);
    }

    @GetMapping("/{id}")
    public PetResponse getPetById(
            @PathVariable Long id,
            Authentication authentication) {
        String username = authentication.getName();
        return petService.getPetById(id, username);
    }

    @PutMapping("/{id}")
    public PetResponse updatePet(
            @PathVariable Long id,
            @Valid @RequestBody PetRequest petRequest,
            Authentication authentication) {
        String username = authentication.getName();
        return petService.updatePet(id, petRequest, username);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePet(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        petService.deletePet(id, username);
    }
}
