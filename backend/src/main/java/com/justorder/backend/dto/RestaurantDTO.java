package com.justorder.backend.dto;

import java.util.List;

public class RestaurantDTO {

    /** Database ID of the restaurant. Null when used as a creation request. */
    private Long id;

    /** Display name of the restaurant. */
    private String name;

    /** Short description of the restaurant shown to customers. */
    private String description;

    /** Contact phone number. */
    private String phone;

    /** Login/contact email address. */
    private String email;

    /**
     * Login password.
     *
     * <p><b>Security note:</b> This field is included for registration 
     * where the client sends a password. It should never be populated in
     * outbound responses (search results, profiles). A future improvement
     * would be to split this into a separate registration-only DTO.</p>
     */
    private String password;

    /**
     * Working hours per day of the week.
     * Format: {@code "HH:mm-HH:mm"}, e.g. {@code "09:00-22:00"}.
     * Null means closed that day.
     */
    private String mondayWorkingHours;
    private String tuesdayWorkingHours;
    private String wednesdayWorkingHours;
    private String thursdayWorkingHours;
    private String fridayWorkingHours;
    private String saturdayWorkingHours;
    private String sundayWorkingHours;

    /**
     * <p>Value range: 0.0 (no reviews yet) to 5.0 (maximum rating).</p>
     */
    private Double averageRating;

    /**
     * List of physical locations or delivery zones for this restaurant.
     * Populated from {@link com.justorder.backend.model.Localization} entities.
     */
    private List<LocalizationDTO> localizations;

    /**
     * The restaurant's full menu.
     */
    private List<DishDTO> dishes;

    /**
     * Names of the cuisine categories this restaurant belongs to
     * (e.g. ["Italian", "Pizza"]).
     */
    private List<String> cuisineCategoryNames;
    public RestaurantDTO() {}

    /**
     * Full constructor used by {@link com.justorder.backend.model.Restaurant#toDTO()}
     * when converting a database entity into an API response.
     *
     * @param id                    Database ID of the restaurant.
     * @param name                  Display name.
     * @param description           Short description.
     * @param phone                 Contact phone number.
     * @param email                 Login email.
     * @param password              Login password (see security note on field).
     * @param mondayWorkingHours    Hours for Monday. Null = closed.
     * @param tuesdayWorkingHours   Hours for Tuesday.
     * @param wednesdayWorkingHours Hours for Wednesday.
     * @param thursdayWorkingHours  Hours for Thursday.
     * @param fridayWorkingHours    Hours for Friday.
     * @param saturdayWorkingHours  Hours for Saturday.
     * @param sundayWorkingHours    Hours for Sunday.
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

    // -------------------------------------------------------------------------
    // Getters & Setters — no business logic, just data access
    // -------------------------------------------------------------------------

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

    /**
     * @return Average customer rating between 0.0 and 5.0.
     *         Null if this DTO was built from a creation request (not from the DB).
     */
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