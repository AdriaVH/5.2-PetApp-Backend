package com.virtualpets.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PetType type;

    @Column(nullable = false)
    private Integer age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    public enum PetType {
        DOG,
        CAT,
        FERRET,
        RABBIT,
        GUINEA_PIG,
        HAMSTER,
        ORNAMENTAL_FISH,
        BIRD_OF_PREY
    }

    // --- domain logic for permissions ---
    public boolean isOwner(User user) {
        return this.owner != null && this.owner.equals(user);
    }

    public boolean canBeManagedBy(User user) {
        return !isOwner(user) && (user == null || !user.hasRole("ROLE_ADMIN"));
    }
}
