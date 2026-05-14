package com.justorder.backend.dto;

import java.util.List;

public class RestaurantDTO {

    private Long id;
    private String name;
    private String description;
    private String phone;
    private String email;
    private String password;
    private String mondayWorkingHours;
    private String tuesdayWorkingHours;
    private String wednesdayWorkingHours;
    private String thursdayWorkingHours;
    private String fridayWorkingHours;
    private String saturdayWorkingHours;
    private String sundayWorkingHours;
    private Double averageRating;
    private List<LocalizationDTO> localizations;
    private List<DishDTO> dishes;
    private List<String> cuisineCategoryNames;
    public RestaurantDTO() {}
    public RestaurantDTO(Long id, String name, String description, String phone, String email, String password,
                         String mondayWorkingHours, String tuesdayWorkingHours, String wednesdayWorkingHours,
                         String thursdayWorkingHours, String fridayWorkingHours, String saturdayWorkingHours,
                         String sundayWorkingHours) {
        this.id = id;
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


    // Getters & Setters
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
    public Double getAverageRating() { return averageRating; }
    public List<LocalizationDTO> getLocalizations() { return localizations; }
    public List<DishDTO> getDishes() { return dishes; }
    public List<String> getCuisineCategoryNames() { return cuisineCategoryNames; }
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
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    public void setLocalizations(List<LocalizationDTO> localizations) { this.localizations = localizations; }
    public void setDishes(List<DishDTO> dishes) { this.dishes = dishes; }
    public void setCuisineCategoryNames(List<String> cuisineCategoryNames) { this.cuisineCategoryNames = cuisineCategoryNames; }
}