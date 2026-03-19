package com.justorder.backend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.justorder.backend.dto.DishDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "dishes")
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private double price;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToMany
    @JoinTable(
        name = "dish_alergens",
        joinColumns = @JoinColumn(name = "dish_id"),
        inverseJoinColumns = @JoinColumn(name = "alergen_id")
    )
    private List<Alergen> alergens = new ArrayList<>();

    public Dish() {
    }
    
    public Dish(String name, String description, double price, Restaurant restaurant) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.restaurant = restaurant;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public Restaurant getRestaurant() { return restaurant; }
    public List<Alergen> getAlergens() { return alergens; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }
    public void setAlergens(List<Alergen> alergens) { this.alergens = alergens; }

    // toDTO
    public DishDTO toDTO() {
        DishDTO dto = new DishDTO(this.id, this.name, this.description, this.price, this.restaurant.getId(), this.restaurant.getName());
        dto.setAlergenNames(this.alergens.stream().map(Alergen::getName).collect(Collectors.toList()));
        return dto;
    }
}
