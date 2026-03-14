package com.justorder.backend.service;

import com.justorder.backend.dto.RestaurantDTO;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for restaurant search and filtering (CA2 — Browse Restaurants).
 *
 * <p><b>Why does this class exist?</b><br>
 * In a layered Spring Boot architecture, each layer has a single responsibility:
 * <ul>
 *   <li><b>Controller</b> — handles HTTP: parses request params, returns status codes.</li>
 *   <li><b>Service (this class)</b> — handles business logic: which filters to apply,
 *       how to convert raw data into API-safe DTOs.</li>
 *   <li><b>Repository</b> — handles database access: runs queries, returns entities.</li>
 * </ul>
 * Without this class, the controller would need to know about JPA entities, conversion
 * logic, and database details — violating the Single Responsibility Principle and making
 * the code much harder to test and maintain.</p>
 *
 * <p><b>Why {@code @Service}?</b><br>
 * The {@code @Service} annotation marks this class as a Spring-managed bean.
 * Spring detects it automatically via component scanning and makes it available
 * for constructor injection in other classes (e.g. the controller).</p>
 *
 * @see com.justorder.backend.controller.RestaurantController
 * @see com.justorder.backend.repository.RestaurantRepository
 */
@Service
public class RestaurantService {

    /**
     * The repository used to query the database.
     *
     * <p>Injected via constructor injection (preferred over {@code @Autowired}
     * field injection because it makes dependencies explicit and allows the
     * class to be instantiated in unit tests without a Spring context).</p>
     */
    private final RestaurantRepository restaurantRepository;

    /**
     * Constructs a {@code RestaurantService} with its required dependency.
     *
     * <p>Spring calls this constructor automatically when the application
     * starts, passing in the repository bean it manages.</p>
     *
     * @param restaurantRepository The JPA repository for restaurant data access.
     */
    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * Returns a list of restaurants matching the given optional filters.
     *
     * <p>This method is the main entry point for CA2 (Browse Restaurants).
     * It delegates the actual database query to the repository, then converts
     * each {@link Restaurant} entity into a {@link RestaurantDTO} before
     * returning (ensuring no raw JPA entities are ever exposed to the
     * controller or serialized into the HTTP response).</p>
     *
     * The controller should not know about JPA entities or how they map to DTOs.
     * That is business logic and belongs in the service layer. The controller
     * only deals in DTOs.</p>
     *
     * <p><b>Any parameter can be {@code null}</b>, which means "no filter on
     * this field". Passing all {@code null}s returns every restaurant in the
     * database — equivalent to "browse all".</p>
     *
     * @param cuisineName Filter by cuisine category name, case-insensitive.
     *                    {@code null} = no cuisine filter.
     * @param minRating   Minimum average rating inclusive (0.0–5.0).
     *                    {@code null} = no rating filter.
     * @param minPrice    Minimum dish price in euros inclusive.
     *                    {@code null} = no lower price bound.
     * @param maxPrice    Maximum dish price in euros inclusive.
     *                    {@code null} = no upper price bound.
     * @return List of matching restaurants as DTOs. Never {@code null},
     *         returns an empty list if no restaurants match the filters.
     */
    public List<RestaurantDTO> searchRestaurants(
            String cuisineName,
            Double minRating,
            Double minPrice,
            Double maxPrice) {

        List<Restaurant> results = restaurantRepository.findWithFilters(
                cuisineName,
                minRating,
                minPrice,
                maxPrice
        );

        // Convert each Restaurant entity → RestaurantDTO using the model's own
        // toDTO() method, which handles nested collections (dishes, localizations,
        // cuisine categories) in one place.
        return results.stream()
                .map(Restaurant::toDTO)
                .collect(Collectors.toList());
    }
}