package com.virtualpets.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Role assigned to a user")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the role", example = "1")
    private Long id;

    @Column(unique = true, nullable = false)
    @Schema(description = "Name of the role", example = "ROLE_ADMIN")
    private String name; // ROLE_ADMIN, ROLE_USER
}
