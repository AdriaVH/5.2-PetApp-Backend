package com.virtualpets.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Pet entity containing information about a pet")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the pet", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Name of the pet", example = "Buddy")
    private String name;

    @Column(nullable = false)
    @Schema(description = "Type of the pet", example = "Dog")
    private String type;

    @Column(nullable = false)
    @Schema(description = "Age of the pet in years", example = "3")
    private Integer age;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @Schema(description = "Owner of the pet")
    private User owner; // <-- Add this
}
