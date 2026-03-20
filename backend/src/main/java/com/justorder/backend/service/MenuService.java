package com.justorder.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.justorder.backend.dto.DishDTO;
import com.justorder.backend.exception.DishConflictException;
import com.justorder.backend.exception.InvalidDishDataException;
import com.justorder.backend.exception.ResourceNotFoundException;
import com.justorder.backend.model.Allergen;
import com.justorder.backend.model.Dish;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.repository.AllergenRepository;
import com.justorder.backend.repository.DishRepository;
import com.justorder.backend.repository.RestaurantRepository;

@Service
public class MenuService {

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private AllergenRepository allergenRepository;

    /**
     * Retrieves all dishes for the admin view.
     */
    public List<DishDTO> getAllDishes() {
        return dishRepository.findAll().stream()
                .map(Dish::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the menu for a specific restaurant.
     */
    public List<DishDTO> getMenu(Long restaurantId) {
        return dishRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(Dish::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new dish for a restaurant.
     */
    @Transactional
    public DishDTO createDish(Long restaurantId, DishDTO dishDTO) {
        validateDishData(dishDTO);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + restaurantId));

        Dish dish = new Dish(dishDTO.getName(), dishDTO.getDescription(), dishDTO.getPrice(), restaurant);

        if (dishDTO.getAllergenNames() != null) {
            List<Allergen> allergens = resolveExistingAllergens(dishDTO.getAllergenNames());
            dish.setAllergens(allergens);
        }

        try {
            return dishRepository.save(dish).toDTO();
        } catch (DataIntegrityViolationException e) {
            throw new DishConflictException("Could not create dish due to data conflict", e);
        }
    }

    /**
     * Updates an existing dish.
     */
    @Transactional
    public DishDTO updateDish(Long dishId, DishDTO dishDTO) {
        validateDishData(dishDTO);

        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found: " + dishId));

        dish.setName(dishDTO.getName());
        dish.setDescription(dishDTO.getDescription());
        dish.setPrice(dishDTO.getPrice());

        if (dishDTO.getAllergenNames() != null) {
            List<Allergen> allergens = resolveExistingAllergens(dishDTO.getAllergenNames());
            dish.setAllergens(allergens);
        }

        try {
            return dishRepository.save(dish).toDTO();
        } catch (DataIntegrityViolationException e) {
            throw new DishConflictException("Could not update dish due to data conflict", e);
        }
    }

    /**
     * Deletes a dish by ID.
     */
    @Transactional
    public void deleteDish(Long dishId) {
        if (!dishRepository.existsById(dishId)) {
            throw new ResourceNotFoundException("Dish not found: " + dishId);
        }
        dishRepository.deleteById(dishId);
    }

    // --- Helper Methods ---

    private void validateDishData(DishDTO dishDTO) {
        if (dishDTO == null) {
            throw new InvalidDishDataException("Dish payload is required");
        }
        if (dishDTO.getName() == null || dishDTO.getName().trim().isEmpty()) {
            throw new InvalidDishDataException("Dish name is required");
        }
        if (dishDTO.getDescription() == null || dishDTO.getDescription().trim().isEmpty()) {
            throw new InvalidDishDataException("Dish description is required");
        }
        if (dishDTO.getPrice() < 0) {
            throw new InvalidDishDataException("Dish price cannot be negative");
        }
    }

    // Resolves the list of allergen names to a list of existing Allergen entities
    private List<Allergen> resolveExistingAllergens(List<String> allergenNames) {
        return allergenNames.stream()
                .map(name -> allergenRepository.findByName(name)
                        .orElseThrow(() -> new InvalidDishDataException("Allergen not found: " + name)))
                .collect(Collectors.toList());
    }
}