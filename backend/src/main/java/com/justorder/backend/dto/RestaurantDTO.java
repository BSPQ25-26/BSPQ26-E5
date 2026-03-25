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

    public RestaurantDTO() {
    }

    /**
     * Constructor used by Restaurant.toDTO() with 13 parameters.
     */
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMondayWorkingHours() {
        return mondayWorkingHours;
    }

    public void setMondayWorkingHours(String mondayWorkingHours) {
        this.mondayWorkingHours = mondayWorkingHours;
    }

    public String getTuesdayWorkingHours() {
        return tuesdayWorkingHours;
    }

    public void setTuesdayWorkingHours(String tuesdayWorkingHours) {
        this.tuesdayWorkingHours = tuesdayWorkingHours;
    }

    public String getWednesdayWorkingHours() {
        return wednesdayWorkingHours;
    }

    public void setWednesdayWorkingHours(String wednesdayWorkingHours) {
        this.wednesdayWorkingHours = wednesdayWorkingHours;
    }

    public String getThursdayWorkingHours() {
        return thursdayWorkingHours;
    }

    public void setThursdayWorkingHours(String thursdayWorkingHours) {
        this.thursdayWorkingHours = thursdayWorkingHours;
    }

    public String getFridayWorkingHours() {
        return fridayWorkingHours;
    }

    public void setFridayWorkingHours(String fridayWorkingHours) {
        this.fridayWorkingHours = fridayWorkingHours;
    }

    public String getSaturdayWorkingHours() {
        return saturdayWorkingHours;
    }

    public void setSaturdayWorkingHours(String saturdayWorkingHours) {
        this.saturdayWorkingHours = saturdayWorkingHours;
    }

    public String getSundayWorkingHours() {
        return sundayWorkingHours;
    }

    public void setSundayWorkingHours(String sundayWorkingHours) {
        this.sundayWorkingHours = sundayWorkingHours;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public List<LocalizationDTO> getLocalizations() {
        return localizations;
    }

    public void setLocalizations(List<LocalizationDTO> localizations) {
        this.localizations = localizations;
    }

    public List<DishDTO> getDishes() {
        return dishes;
    }

    public void setDishes(List<DishDTO> dishes) {
        this.dishes = dishes;
    }

    public List<String> getCuisineCategoryNames() {
        return cuisineCategoryNames;
    }

    public void setCuisineCategoryNames(List<String> cuisineCategoryNames) {
        this.cuisineCategoryNames = cuisineCategoryNames;
    }
}