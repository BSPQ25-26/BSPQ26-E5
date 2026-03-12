package com.justorder.backend.model;

import com.justorder.backend.dto.CuisineCategoryDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cuisine_categories")
public class CuisineCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    // Constructors
    public CuisineCategory() {
    }

    public CuisineCategory(String name) {
        this.name = name;
    }

    public CuisineCategory(String name, String description) {
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
    public CuisineCategoryDTO toDTO() {
        return new CuisineCategoryDTO(this.name, this.description);
    }
}
