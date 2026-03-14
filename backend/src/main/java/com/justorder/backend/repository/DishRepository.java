package com.justorder.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.justorder.backend.model.Dish;

public interface DishRepository extends JpaRepository<Dish, Long> {
    // Finds all dishes that belong to a specific restaurant by its id
    List<Dish> findByRestaurantId(Long restaurantId);
}
