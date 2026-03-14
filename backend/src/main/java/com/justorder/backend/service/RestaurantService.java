package com.justorder.backend.service;

import com.justorder.backend.dto.RestaurantDTO;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    /**
     * The repository used to query the database.
     */
    private final RestaurantRepository restaurantRepository;

    /**
     * Constructs a {@code RestaurantService} with its required dependency.
     *
     * @param restaurantRepository The JPA repository for restaurant data access.
     */
    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * Returns a list of restaurants matching the given optional filters.
     *
     * @param cuisineName Filter by cuisine category name, case-insensitive.
     *                    {@code null} = no cuisine filter.
     * @param minRating   Minimum average rating inclusive (0.0–5.0).
     *                    {@code null} = no rating filter.
     * @param minPrice    Minimum dish price in euros inclusive.
     *                    {@code null} = no lower price bound.
     * @param maxPrice    Maximum dish price in euros inclusive.
     *                    {@code null} = no upper price bound.
     * @return List of matching restaurants as DTOs. 
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

        return results.stream()
                .map(Restaurant::toDTO)
                .collect(Collectors.toList());
    }
}