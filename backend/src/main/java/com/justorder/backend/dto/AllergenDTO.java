package com.justorder.backend.dto;

public class AllergenDTO {

    private String name;
    private String description;

    public AllergenDTO() {
    }

    public AllergenDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
}