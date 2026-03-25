package com.justorder.backend.dto;

import java.util.List;

public class DishDTO {

    private Long id;
    private String name;
    private String description;
    private double price;
    private String image;
    private Long restaurantId;
    private String restaurantName;
    private List<String> allergenNames;
    private List<Long> allergenIds;
    private List<Long> categoryIds;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public List<String> getAllergenNames() {
        return allergenNames;
    }

    public void setAllergenNames(List<String> allergenNames) {
        this.allergenNames = allergenNames;
    }

    public List<Long> getAllergenIds() {
        return allergenIds;
    }

    public void setAllergenIds(List<Long> allergenIds) {
        this.allergenIds = allergenIds;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }
}