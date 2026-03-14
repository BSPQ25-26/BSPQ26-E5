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

/**
 * JPA entity representing a restaurant registered on the Just Order platform.
 *
 * <p>Maps directly to the {@code restaurants} table in PostgreSQL. Each
 * restaurant can have multiple dishes, multiple localizations (addresses /
 * delivery zones), and belong to multiple cuisine categories.</p>
 *
 * <p>Restaurants are one of the three main user types alongside Customers
 * and Riders. They are registered via IAM-2 and manage their menu via CA1.</p>
 *
 * <p><b>Design note:</b> The {@code password} field is currently stored as
 * plaintext for sprint simplicity. Before any production deployment it must
 * be hashed (e.g. BCrypt).</p>
 *
 * @see com.justorder.backend.dto.RestaurantDTO
 * @see com.justorder.backend.service.RestaurantService
 * @see com.justorder.backend.repository.RestaurantRepository
 */
@Entity
@Table(name = "restaurants")
public class Restaurant {

    /**
     * Auto-generated primary key.
     * Never set manually — managed by PostgreSQL via IDENTITY (auto-increment).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Display name of the restaurant shown to customers in search results. */
    private String name;

    /** Short description of the restaurant, e.g. cuisine style or specialty. */
    private String description;

    /** Contact phone number. Stored as String to support international formats. */
    private String phone;

    /**
     * Email address used for login and notifications.
     * Must be unique across all restaurants — enforced by
     * {@link com.justorder.backend.repository.RestaurantRepository#existsByEmail(String)}.
     */
    private String email;

    /**
     * Login password for this restaurant account.
     *
     * <p><b>TODO:</b> Must be hashed with BCrypt before storage.
     * Plaintext passwords are a critical security risk.</p>
     */
    private String password;

    /**
     * Working hours per day of the week, stored as a human-readable string.
     *
     * <p>Format: {@code "HH:mm-HH:mm"}, e.g. {@code "09:00-22:00"}.
     * A {@code null} or empty value means the restaurant is closed that day.</p>
     */
    private String mondayWorkingHours;
    private String tuesdayWorkingHours;
    private String wednesdayWorkingHours;
    private String thursdayWorkingHours;
    private String fridayWorkingHours;
    private String saturdayWorkingHours;
    private String sundayWorkingHours;

    /**
     * Pre-computed average customer rating on a scale of 0.0 to 5.0.
     *
     * <p>Defaults to 0.0 on first registration. Must be recalculated and
     * updated each time a customer submits a review (user story RE1).</p>
     */
    private Double averageRating = 0.0;

    /**
     * Physical locations or delivery zones associated with this restaurant.
     *
     * <p>{@code CascadeType.ALL} and {@code orphanRemoval = true} mean that
     * localizations are automatically created, updated, and deleted alongside
     * the restaurant (no need to manage them in a separate repository call).</p>
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Localization> localizations = new ArrayList<>();

    /**
     * The dishes offered by this restaurant (its menu).
     *
     * <p>{@code mappedBy = "restaurant"} means the foreign key column
     * ({@code restaurant_id}) lives on the {@code dishes} table, not here.
     * Cascade and orphanRemoval ensure dishes are deleted when the restaurant
     * is deleted or when a dish is removed from this list.</p>
     *
     * @see com.justorder.backend.model.Dish
     */
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dish> dishes = new ArrayList<>();

    /**
     * Cuisine categories this restaurant belongs to (e.g. Italian, Japanese).
     *
     * <p>Many-to-many because a restaurant can belong to multiple categories
     * and a category can contain many restaurants. The join table
     * {@code restaurant_cuisine_categories} manages this relationship in the DB.</p>
     *
     * <p>Used by CA2 to support filtering restaurants by cuisine type.</p>
     *
     * @see com.justorder.backend.model.CuisineCategory
     */
    @ManyToMany
    @JoinTable(
        name = "restaurant_cuisine_categories",
        joinColumns = @JoinColumn(name = "restaurant_id"),
        inverseJoinColumns = @JoinColumn(name = "cuisine_category_id")
    )
    private List<CuisineCategory> cuisineCategories = new ArrayList<>();

    /**
     * No-argument constructor required by JPA.
     * Do not use directly in application code (use the parameterized constructor).
     */
    public Restaurant() {}

    /**
     * Creates a new Restaurant with all required registration fields.
     *
     * <p>Called during restaurant registration (IAM-2) after validating the
     * incoming {@link RestaurantDTO}. The {@code id} is not set here because
     * it is auto-generated by the database on first save.</p>
     *
     * @param name                  Display name of the restaurant.
     * @param description           Short description shown to customers.
     * @param phone                 Contact phone number.
     * @param email                 Login email, must be unique across restaurants.
     * @param password              Login password (must be hashed before passing in).
     * @param mondayWorkingHours    Hours for Monday, format "HH:mm-HH:mm". Null = closed.
     * @param tuesdayWorkingHours   Hours for Tuesday.
     * @param wednesdayWorkingHours Hours for Wednesday.
     * @param thursdayWorkingHours  Hours for Thursday.
     * @param fridayWorkingHours    Hours for Friday.
     * @param saturdayWorkingHours  Hours for Saturday.
     * @param sundayWorkingHours    Hours for Sunday.
     */
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

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /** @return The auto-generated database ID. */
    public Long getId() { return id; }

    /** @return The display name of the restaurant. */
    public String getName() { return name; }

    /** @return Short description of the restaurant. */
    public String getDescription() { return description; }

    /** @return Contact phone number. */
    public String getPhone() { return phone; }

    /** @return Login email address. */
    public String getEmail() { return email; }

    /** @return Login password (see field Javadoc, should be hashed). */
    public String getPassword() { return password; }

    /** @return Working hours for Monday, e.g. "09:00-22:00". Null if closed. */
    public String getMondayWorkingHours() { return mondayWorkingHours; }
    public String getTuesdayWorkingHours() { return tuesdayWorkingHours; }
    public String getWednesdayWorkingHours() { return wednesdayWorkingHours; }
    public String getThursdayWorkingHours() { return thursdayWorkingHours; }
    public String getFridayWorkingHours() { return fridayWorkingHours; }
    public String getSaturdayWorkingHours() { return saturdayWorkingHours; }
    public String getSundayWorkingHours() { return sundayWorkingHours; }

    /**
     * @return Pre-computed average customer rating between 0.0 and 5.0.
     *         Returns 0.0 if no reviews have been submitted yet.
     */
    public Double getAverageRating() { return averageRating; }

    /** @return List of physical locations or delivery zones. */
    public List<Localization> getLocalizations() { return localizations; }

    /** @return Full list of dishes (the restaurant's menu). */
    public List<Dish> getDishes() { return dishes; }

    /** @return Cuisine categories this restaurant belongs to. */
    public List<CuisineCategory> getCuisineCategories() { return cuisineCategories; }

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

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

    /**
     * Updates the pre-computed average rating for this restaurant.
     *
     * <p><b>When to call:</b> Only from the review service (RE1) after a new
     * customer review is persisted. Do not call this directly from controllers.</p>
     *
     * @param averageRating Newly computed average, must be between 0.0 and 5.0.
     */
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public void setLocalizations(List<Localization> localizations) { this.localizations = localizations; }
    public void setDishes(List<Dish> dishes) { this.dishes = dishes; }
    public void setCuisineCategories(List<CuisineCategory> cuisineCategories) { this.cuisineCategories = cuisineCategories; }

    // -------------------------------------------------------------------------
    // Conversion
    // -------------------------------------------------------------------------

    /**
     * Converts this entity into a {@link RestaurantDTO} safe to return via the REST API.
     *
     * <p><b>Why convert to DTO instead of returning the entity directly?</b>
     * <ul>
     *   <li>Entities may contain sensitive fields (e.g. {@code password}) that
     *       must never be sent over the wire.</li>
     *   <li>JPA lazy-loaded collections cause {@code LazyInitializationException}
     *       when Jackson tries to serialize them outside a transaction.</li>
     *   <li>DTOs give us explicit control over the API contract, independent
     *       of how the database model evolves.</li>
     * </ul>
     *
     * <p>All nested collections (dishes, localizations, categories) are converted
     * eagerly here to avoid session lifecycle issues.</p>
     *
     * @return A fully populated {@link RestaurantDTO} for this restaurant.
     */
    public RestaurantDTO toDTO() {
        RestaurantDTO dto = new RestaurantDTO(
            this.id, this.name, this.description, this.phone, this.email, this.password,
            this.mondayWorkingHours, this.tuesdayWorkingHours, this.wednesdayWorkingHours,
            this.thursdayWorkingHours, this.fridayWorkingHours, this.saturdayWorkingHours,
            this.sundayWorkingHours
        );
        dto.setAverageRating(this.averageRating);
        dto.setLocalizations(this.localizations.stream().map(Localization::toDTO).collect(Collectors.toList()));
        dto.setDishes(this.dishes.stream().map(Dish::toDTO).collect(Collectors.toList()));
        dto.setCuisineCategoryNames(this.cuisineCategories.stream().map(CuisineCategory::getName).collect(Collectors.toList()));
        return dto;
    }
}