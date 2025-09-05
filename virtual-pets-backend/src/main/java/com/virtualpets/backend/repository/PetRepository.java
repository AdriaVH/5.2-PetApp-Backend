package com.virtualpets.backend.repository;

import com.virtualpets.backend.model.Pet;
import com.virtualpets.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    Page<Pet> findByOwner(User owner, Pageable pageable);
}