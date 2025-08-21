package com.virtualpets.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "pets")
@Schema(description = "Represents a virtual pet owned by a user")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the pet", example = "1")
    private Long id;

    @Schema(description = "Name of the pet", example = "Fluffy")
    private String name;

    @Schema(description = "Type of the pet", example = "Dragon")
    private String type;

    @Schema(description = "Color of the pet", example = "Red")
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @Schema(description = "Owner of the pet (User)")
    private User owner;
}
