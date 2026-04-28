package com.justorder.backend.service;

import com.justorder.backend.dto.LocalizationDTO;
import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.dto.RestaurantDashboardDTO;
import com.justorder.backend.dto.RestaurantDTO;
import com.justorder.backend.dto.RestaurantProfileUpdateDTO;
import com.justorder.backend.exception.ResourceNotFoundException;
import com.justorder.backend.model.CuisineCategory;
import com.justorder.backend.model.Localization;
import com.justorder.backend.model.Order;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.repository.CuisineCategoryRepository;
import com.justorder.backend.repository.OrderRepository;
import com.justorder.backend.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final CuisineCategoryRepository cuisineCategoryRepository;
    private final OrderRepository orderRepository;


    public RestaurantService(RestaurantRepository restaurantRepository,
                             CuisineCategoryRepository cuisineCategoryRepository,
                             OrderRepository orderRepository) {
        this.restaurantRepository = restaurantRepository;
        this.cuisineCategoryRepository = cuisineCategoryRepository;
        this.orderRepository = orderRepository;
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

    @Transactional(readOnly = true)
    public RestaurantDTO getRestaurantProfile(Long restaurantId) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);
        return sanitizeRestaurantDTO(restaurant.toDTO());
    }

    @Transactional
    public RestaurantDTO updateRestaurantProfile(Long restaurantId, RestaurantProfileUpdateDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Profile update payload is required");
        }

        Restaurant restaurant = getRestaurantOrThrow(restaurantId);

        if (request.getName() != null) {
            restaurant.setName(validateRequiredText(request.getName(), "name"));
        }
        if (request.getDescription() != null) {
            restaurant.setDescription(validateRequiredText(request.getDescription(), "description"));
        }
        if (request.getPhone() != null) {
            restaurant.setPhone(validateRequiredText(request.getPhone(), "phone"));
        }

        if (request.getMondayWorkingHours() != null) {
            restaurant.setMondayWorkingHours(validateWorkingHours(request.getMondayWorkingHours(), "mondayWorkingHours"));
        }
        if (request.getTuesdayWorkingHours() != null) {
            restaurant.setTuesdayWorkingHours(validateWorkingHours(request.getTuesdayWorkingHours(), "tuesdayWorkingHours"));
        }
        if (request.getWednesdayWorkingHours() != null) {
            restaurant.setWednesdayWorkingHours(validateWorkingHours(request.getWednesdayWorkingHours(), "wednesdayWorkingHours"));
        }
        if (request.getThursdayWorkingHours() != null) {
            restaurant.setThursdayWorkingHours(validateWorkingHours(request.getThursdayWorkingHours(), "thursdayWorkingHours"));
        }
        if (request.getFridayWorkingHours() != null) {
            restaurant.setFridayWorkingHours(validateWorkingHours(request.getFridayWorkingHours(), "fridayWorkingHours"));
        }
        if (request.getSaturdayWorkingHours() != null) {
            restaurant.setSaturdayWorkingHours(validateWorkingHours(request.getSaturdayWorkingHours(), "saturdayWorkingHours"));
        }
        if (request.getSundayWorkingHours() != null) {
            restaurant.setSundayWorkingHours(validateWorkingHours(request.getSundayWorkingHours(), "sundayWorkingHours"));
        }

        if (request.getLocalizations() != null) {
            updateLocalizations(restaurant, toLocalizationEntities(request.getLocalizations()));
        }
        if (request.getCuisineCategoryNames() != null) {
            updateCuisineCategories(restaurant, toCuisineCategoryEntities(request.getCuisineCategoryNames()));
        }

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return sanitizeRestaurantDTO(savedRestaurant.toDTO());
    }

    @Transactional(readOnly = true)
    public RestaurantDashboardDTO getRestaurantDashboard(Long restaurantId) {
        getRestaurantOrThrow(restaurantId);

        List<Order> restaurantOrders = orderRepository.findAll().stream()
                .filter(order -> isOrderFromRestaurant(order, restaurantId))
                .toList();

        long totalOrders = restaurantOrders.size();
        long cancelledOrders = countByStatus(restaurantOrders, "Cancelled");
        long deliveredOrders = countByStatus(restaurantOrders, "Delivered");
        long activeOrders = totalOrders - cancelledOrders;

        double totalRevenue = restaurantOrders.stream()
                .filter(order -> !isStatus(order, "Cancelled"))
                .mapToDouble(Order::getTotalPrice)
                .sum();

        double totalRefunded = restaurantOrders.stream()
                .filter(order -> isStatus(order, "Cancelled"))
                .mapToDouble(Order::getTotalPrice)
                .sum();

        List<OrderDTO> recentOrders = restaurantOrders.stream()
                .sorted(Comparator.comparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(Order::toDTO)
                .toList();

        return new RestaurantDashboardDTO(
                restaurantId,
                totalOrders,
                activeOrders,
                cancelledOrders,
                deliveredOrders,
                totalRevenue,
                totalRefunded,
                recentOrders
        );
    }

    private Restaurant getRestaurantOrThrow(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));
    }

    private boolean isOrderFromRestaurant(Order order, Long restaurantId) {
        if (order.getDishes() == null) {
            return false;
        }

        return order.getDishes().stream()
                .anyMatch(dish -> dish.getRestaurant() != null
                        && dish.getRestaurant().getId() != null
                        && dish.getRestaurant().getId().equals(restaurantId));
    }

    private boolean isStatus(Order order, String status) {
        return order.getStatus() != null
                && order.getStatus().getStatus() != null
                && order.getStatus().getStatus().equalsIgnoreCase(status);
    }

    private long countByStatus(List<Order> orders, String status) {
        return orders.stream()
                .filter(order -> isStatus(order, status))
                .count();
    }

    private String validateRequiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value.trim();
    }

    private String validateWorkingHours(String workingHours, String fieldName) {
        String value = validateRequiredText(workingHours, fieldName);
        String[] parts = value.split("-");
        if (parts.length != 2 || !parts[0].matches("\\d{2}:\\d{2}") || !parts[1].matches("\\d{2}:\\d{2}")) {
            throw new IllegalArgumentException(fieldName + " must be in format HH:mm-HH:mm");
        }
        return value;
    }

    private List<Localization> toLocalizationEntities(List<LocalizationDTO> localizations) {
        if (localizations.isEmpty()) {
            throw new IllegalArgumentException("At least one localization is required");
        }

        return localizations.stream()
                .map(Localization::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void updateLocalizations(Restaurant restaurant, List<Localization> newLocalizations) {
        restaurant.getLocalizations().clear();
        restaurant.getLocalizations().addAll(newLocalizations);
    }

    private List<CuisineCategory> toCuisineCategoryEntities(List<String> cuisineCategoryNames) {
        if (cuisineCategoryNames.isEmpty()) {
            throw new IllegalArgumentException("At least one cuisine category is required");
        }

        return cuisineCategoryNames.stream()
                .map(name -> cuisineCategoryRepository.findByName(name)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown cuisine category: " + name)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void updateCuisineCategories(Restaurant restaurant, List<CuisineCategory> newCuisineCategories) {
        restaurant.getCuisineCategories().clear();
        restaurant.getCuisineCategories().addAll(newCuisineCategories);
    }
    private RestaurantDTO sanitizeRestaurantDTO(RestaurantDTO dto) {
        dto.setPassword(null);
        return dto;
    }   
}