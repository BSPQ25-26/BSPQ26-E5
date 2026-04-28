package com.justorder.backend.dto;

import java.util.List;

public class RestaurantProfileUpdateDTO {

    private String name;
    private String description;
    private String phone;
    private String mondayWorkingHours;
    private String tuesdayWorkingHours;
    private String wednesdayWorkingHours;
    private String thursdayWorkingHours;
    private String fridayWorkingHours;
    private String saturdayWorkingHours;
    private String sundayWorkingHours;
    private List<LocalizationDTO> localizations;
    private List<String> cuisineCategoryNames;

    public RestaurantProfileUpdateDTO() {
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

    public List<LocalizationDTO> getLocalizations() {
        return localizations;
    }

    public void setLocalizations(List<LocalizationDTO> localizations) {
        this.localizations = localizations;
    }

    public List<String> getCuisineCategoryNames() {
        return cuisineCategoryNames;
    }

    public void setCuisineCategoryNames(List<String> cuisineCategoryNames) {
        this.cuisineCategoryNames = cuisineCategoryNames;
    }
}
