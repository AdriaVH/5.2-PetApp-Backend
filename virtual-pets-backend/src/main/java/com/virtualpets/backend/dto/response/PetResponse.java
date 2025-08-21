package com.virtualpets.backend.dto.response;

public record PetResponse(Long id, String name, String type, String color, String ownerUsername) {}
