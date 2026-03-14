package com.justorder.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.justorder.backend.dto.DishDTO;
import com.justorder.backend.exception.DishConflictException;
import com.justorder.backend.exception.InvalidDishDataException;
import com.justorder.backend.exception.ResourceNotFoundException;
import com.justorder.backend.model.Alergen;
import com.justorder.backend.model.Dish;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.repository.AlergenRepository;
import com.justorder.backend.repository.DishRepository;
import com.justorder.backend.repository.RestaurantRepository;

@Service
public class MenuService {

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private AlergenRepository alergenRepository;

    // This method is used by the GET /api/restaurants/{restaurantId}/menu endpoint
    public List<DishDTO> getMenu(Long restaurantId) {
        return dishRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(Dish::toDTO)
                .collect(Collectors.toList());
    }

    // This method is used by the POST /api/dishes/{restaurantId} endpoint
    public DishDTO createDish(Long restaurantId, DishDTO dishDTO) {
        validateDishData(dishDTO);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + restaurantId));

        Dish dish = new Dish(dishDTO.getName(), dishDTO.getDescription(), dishDTO.getPrice(), restaurant);

        if (dishDTO.getAlergenNames() != null) {
            List<Alergen> alergens = dishDTO.getAlergenNames().stream()
                    .map(name -> alergenRepository.findByName(name)
                            .orElseGet(() -> alergenRepository.save(new Alergen(name))))
                    .collect(Collectors.toList());
            dish.setAlergens(alergens);
        }

        try {
            return dishRepository.save(dish).toDTO();
        } catch (DataIntegrityViolationException e) {
            throw new DishConflictException("Could not create dish due to data conflict", e);
        }
    }

    // This method is used by the PUT /api/dishes/{dishId} endpoint
    public DishDTO updateDish(Long dishId, DishDTO dishDTO) {
        validateDishData(dishDTO);

        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found: " + dishId));

        dish.setName(dishDTO.getName());
        dish.setDescription(dishDTO.getDescription());
        dish.setPrice(dishDTO.getPrice());

        if (dishDTO.getAlergenNames() != null) {
            List<Alergen> alergens = dishDTO.getAlergenNames().stream()
                    .map(name -> alergenRepository.findByName(name)
                            .orElseGet(() -> alergenRepository.save(new Alergen(name))))
                    .collect(Collectors.toList());
            dish.setAlergens(alergens);
        }

        try {
            return dishRepository.save(dish).toDTO();
        } catch (DataIntegrityViolationException e) {
            throw new DishConflictException("Could not update dish due to data conflict", e);
        }
    }

    // This method is used by the DELETE /api/dishes/{dishId} endpoint
    public void deleteDish(Long dishId) {
        if (!dishRepository.existsById(dishId)) {
            throw new ResourceNotFoundException("Dish not found: " + dishId);
        }
        dishRepository.deleteById(dishId);
    }

    // Validates the dish data and throws an exception if any required field is missing or invalid
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
}

