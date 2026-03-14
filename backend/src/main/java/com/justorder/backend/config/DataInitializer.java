package com.justorder.backend.config;

import com.justorder.backend.model.Admin;
import com.justorder.backend.model.Alergen;
import com.justorder.backend.model.CuisineCategory;
import com.justorder.backend.model.Dish;
import com.justorder.backend.model.OrderStatus;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.repository.AdminRepository;
import com.justorder.backend.repository.AlergenRepository;
import com.justorder.backend.repository.CuisineCategoryRepository;
import com.justorder.backend.repository.DishRepository;
import com.justorder.backend.repository.OrderStatusRepository;
import com.justorder.backend.repository.RestaurantRepository;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CuisineCategoryRepository cuisineCategoryRepository;

    @Autowired
    private AlergenRepository alergenRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Override
    public void run(String... args) throws Exception {

        // Initialización de los datos de prueba
        if (adminRepository.count() == 0) {
            Admin superAdmin = new Admin();
            superAdmin.setName("Super Admin");
            superAdmin.setEmail("admin@justorder.com");
            superAdmin.setPassword(passwordEncoder.encode("admin123")); 
            adminRepository.save(superAdmin);
        }

        // Inicialización de categorías de cocina
        if (cuisineCategoryRepository.count() == 0) {
            cuisineCategoryRepository.save(new CuisineCategory("Italian"));
            cuisineCategoryRepository.save(new CuisineCategory("Chinese"));
            cuisineCategoryRepository.save(new CuisineCategory("Mexican"));
            cuisineCategoryRepository.save(new CuisineCategory("Indian"));
            cuisineCategoryRepository.save(new CuisineCategory("Japanese"));
            cuisineCategoryRepository.save(new CuisineCategory("Mediterranean"));
        }

        // Inicialización de alérgenos
        if (alergenRepository.count() == 0) {
            alergenRepository.save(new Alergen("Gluten", "Cereals containing gluten"));
            alergenRepository.save(new Alergen("Lactose", "Milk and dairy products"));
            alergenRepository.save(new Alergen("Peanuts", "Peanuts and peanut-based products"));
            alergenRepository.save(new Alergen("Shellfish", "Crustaceans and shellfish products"));
            alergenRepository.save(new Alergen("Soy", "Soybeans and soy-based products"));
            alergenRepository.save(new Alergen("Eggs", "Eggs and egg-based products"));
        }

        // Inicialización de restaurantes y platos para los tests de menú.
        if (restaurantRepository.count() == 0) {
            Restaurant restaurant1 = new Restaurant(
                "La Marina Bistro",
                "Mediterranean restaurant near the seafront",
                "+34 900 000 001",
                "lamarina@justorder.com",
                passwordEncoder.encode("restaurant123"),
                "09:00-22:00",
                "09:00-22:00",
                "09:00-22:00",
                "09:00-22:00",
                "09:00-23:00",
                "10:00-23:00",
                "10:00-22:00"
            );
            restaurant1 = restaurantRepository.save(restaurant1);

            Restaurant restaurant2 = new Restaurant(
                "Green Bowl Kitchen",
                "Healthy bowls and plant-forward comfort food",
                "+34 900 000 002",
                "greenbowl@justorder.com",
                passwordEncoder.encode("restaurant123"),
                "11:00-22:00",
                "11:00-22:00",
                "11:00-22:00",
                "11:00-22:00",
                "11:00-23:00",
                "11:00-23:00",
                "11:00-21:00"
            );
            restaurant2 = restaurantRepository.save(restaurant2);

            if (dishRepository.count() == 0) {
            Alergen gluten = alergenRepository.findByName("Gluten")
                .orElseThrow(() -> new IllegalStateException("Alergen Gluten not found"));
            Alergen lactose = alergenRepository.findByName("Lactose")
                .orElseThrow(() -> new IllegalStateException("Alergen Lactose not found"));
            Alergen soy = alergenRepository.findByName("Soy")
                .orElseThrow(() -> new IllegalStateException("Alergen Soy not found"));
            Alergen eggs = alergenRepository.findByName("Eggs")
                .orElseThrow(() -> new IllegalStateException("Alergen Eggs not found"));
            Alergen peanuts = alergenRepository.findByName("Peanuts")
                .orElseThrow(() -> new IllegalStateException("Alergen Peanuts not found"));

            Dish dish1 = new Dish("Four Cheese Pizza", "Stone-baked pizza with four cheeses", 23.0, restaurant1);
            dish1.setAlergens(Arrays.asList(gluten, lactose));

            Dish dish2 = new Dish("Grilled Salmon", "Grilled salmon fillet with herbs", 25.0, restaurant1);
            dish2.setAlergens(List.of());

            Dish dish3 = new Dish("Thai Tofu Bowl", "Tofu bowl with peanut sauce and scrambled egg", 19.5, restaurant2);
            dish3.setAlergens(Arrays.asList(soy, eggs, peanuts));

            dishRepository.save(dish1);
            dishRepository.save(dish2);
            dishRepository.save(dish3);
            }
        }

        // Inicialización de estados de pedido
        if (orderStatusRepository.count() == 0) {
            orderStatusRepository.save(new OrderStatus("Pending"));
            orderStatusRepository.save(new OrderStatus("Confirmed"));
            orderStatusRepository.save(new OrderStatus("Preparing"));
            orderStatusRepository.save(new OrderStatus("Out for Delivery"));
            orderStatusRepository.save(new OrderStatus("Delivered"));
            orderStatusRepository.save(new OrderStatus("Cancelled"));
        }
    }
}