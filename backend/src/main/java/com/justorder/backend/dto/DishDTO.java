package com.justorder.backend.dto;

import java.util.List;

public class DishDTO {

    private Long id;
    private String name;
    private String description;
    private double price;
    private Long restaurantId;
    private String restaurantName;
    private List<String> alergenNames;

    public DishDTO() {
    }

    public DishDTO(Long id, String name, String description, double price, Long restaurantId, String restaurantName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public Long getRestaurantId() { return restaurantId; }
    public String getRestaurantName() { return restaurantName; }
    public List<String> getAlergenNames() { return alergenNames; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public void setAlergenNames(List<String> alergenNames) { this.alergenNames = alergenNames; }
}
