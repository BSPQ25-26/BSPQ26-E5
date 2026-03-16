package com.justorder.backend.service;

import com.justorder.backend.dto.RestaurantDTO;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;


    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }


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