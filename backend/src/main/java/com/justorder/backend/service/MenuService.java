package com.justorder.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justorder.backend.dto.DishDTO;
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

    public List<DishDTO> getMenu(Long restaurantId) {
        return dishRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(Dish::toDTO)
                .collect(Collectors.toList());
    }

    public DishDTO createDish(Long restaurantId, DishDTO dishDTO) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found: " + restaurantId));

        Dish dish = new Dish(dishDTO.getName(), dishDTO.getDescription(), dishDTO.getPrice(), restaurant);

        if (dishDTO.getAlergenNames() != null) {
            List<Alergen> alergens = dishDTO.getAlergenNames().stream()
                    .map(name -> alergenRepository.findByName(name)
                            .orElseGet(() -> alergenRepository.save(new Alergen(name))))
                    .collect(Collectors.toList());
            dish.setAlergens(alergens);
        }

        return dishRepository.save(dish).toDTO();
    }

    public DishDTO updateDish(Long dishId, DishDTO dishDTO) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new RuntimeException("Dish not found: " + dishId));

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

        return dishRepository.save(dish).toDTO();
    }

    public void deleteDish(Long dishId) {
        if (!dishRepository.existsById(dishId)) {
            throw new RuntimeException("Dish not found: " + dishId);
        }
        dishRepository.deleteById(dishId);
    }
}

