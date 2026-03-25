package com.justorder.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.justorder.backend.model.Dish;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {

    /**
     * Finds all dishes that belong to a specific restaurant by its id.
     * Required by MenuService to retrieve the restaurant's menu.
     */
    List<Dish> findByRestaurantId(Long restaurantId);
}