package com.justorder.backend.model;

import com.justorder.backend.dto.AllergenDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "allergens")
public class Allergen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    // Constructors
    public Allergen() {
    }

    public Allergen(String name) {
        this.name = name;
    }

    public Allergen(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }

    // toDTO
    public AllergenDTO toDTO() {
        return new AllergenDTO(this.id, this.name, this.description);
    }
}