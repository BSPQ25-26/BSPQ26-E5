package com.justorder.backend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.justorder.backend.dto.RestaurantDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String phone;
    private String email;
    private String password;
    private String mondayWorkingHours; // e.g. "09:00-18:00"
    private String tuesdayWorkingHours;
    private String wednesdayWorkingHours;
    private String thursdayWorkingHours;
    private String fridayWorkingHours;
    private String saturdayWorkingHours;
    private String sundayWorkingHours;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Localization> localizations = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dish> dishes = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "restaurant_cuisine_categories",
        joinColumns = @JoinColumn(name = "restaurant_id"),
        inverseJoinColumns = @JoinColumn(name = "cuisine_category_id")
    )
    private List<CuisineCategory> cuisineCategories = new ArrayList<>();

    // Constructors
    public Restaurant() {
    }
    public Restaurant(String name, String description, String phone, String email, String password,
                      String mondayWorkingHours, String tuesdayWorkingHours, String wednesdayWorkingHours,
                      String thursdayWorkingHours, String fridayWorkingHours, String saturdayWorkingHours,
                      String sundayWorkingHours) {
        this.name = name;
        this.description = description;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.mondayWorkingHours = mondayWorkingHours;
        this.tuesdayWorkingHours = tuesdayWorkingHours;
        this.wednesdayWorkingHours = wednesdayWorkingHours;
        this.thursdayWorkingHours = thursdayWorkingHours;
        this.fridayWorkingHours = fridayWorkingHours;
        this.saturdayWorkingHours = saturdayWorkingHours;
        this.sundayWorkingHours = sundayWorkingHours;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getMondayWorkingHours() { return mondayWorkingHours; }
    public String getTuesdayWorkingHours() { return tuesdayWorkingHours; }
    public String getWednesdayWorkingHours() { return wednesdayWorkingHours; }
    public String getThursdayWorkingHours() { return thursdayWorkingHours; }
    public String getFridayWorkingHours() { return fridayWorkingHours; }
    public String getSaturdayWorkingHours() { return saturdayWorkingHours; }
    public String getSundayWorkingHours() { return sundayWorkingHours; }
    public List<Localization> getLocalizations() { return localizations; }
    public List<Dish> getDishes() { return dishes; }
    public List<CuisineCategory> getCuisineCategories() { return cuisineCategories; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setMondayWorkingHours(String mondayWorkingHours) { this.mondayWorkingHours = mondayWorkingHours; }
    public void setTuesdayWorkingHours(String tuesdayWorkingHours) { this.tuesdayWorkingHours = tuesdayWorkingHours; }
    public void setWednesdayWorkingHours(String wednesdayWorkingHours) { this.wednesdayWorkingHours = wednesdayWorkingHours; }
    public void setThursdayWorkingHours(String thursdayWorkingHours) { this.thursdayWorkingHours = thursdayWorkingHours; }
    public void setFridayWorkingHours(String fridayWorkingHours) { this.fridayWorkingHours = fridayWorkingHours; }
    public void setSaturdayWorkingHours(String saturdayWorkingHours) { this.saturdayWorkingHours = saturdayWorkingHours; }
    public void setSundayWorkingHours(String sundayWorkingHours) { this.sundayWorkingHours = sundayWorkingHours; }
    public void setLocalizations(List<Localization> localizations) { this.localizations = localizations; }
    public void setDishes(List<Dish> dishes) { this.dishes = dishes; }
    public void setCuisineCategories(List<CuisineCategory> cuisineCategories) { this.cuisineCategories = cuisineCategories; }

    // toDTO
    public RestaurantDTO toDTO() {
        RestaurantDTO dto = new RestaurantDTO(
            this.id, this.name, this.description, this.phone, this.email, this.password,
            this.mondayWorkingHours, this.tuesdayWorkingHours, this.wednesdayWorkingHours,
            this.thursdayWorkingHours, this.fridayWorkingHours, this.saturdayWorkingHours,
            this.sundayWorkingHours
        );
        dto.setLocalizations(this.localizations.stream().map(Localization::toDTO).collect(Collectors.toList()));
        dto.setDishes(this.dishes.stream().map(Dish::toDTO).collect(Collectors.toList()));
        dto.setCuisineCategoryNames(this.cuisineCategories.stream().map(CuisineCategory::getName).collect(Collectors.toList()));
        return dto;
    }
}