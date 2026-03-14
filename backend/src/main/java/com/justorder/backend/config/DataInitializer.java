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
import com.justorder.backend.repository.OrderStatusRepository;
import com.justorder.backend.repository.RestaurantRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

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
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Override
    public void run(String... args) throws Exception {

        // Admin
        if (adminRepository.count() == 0) {
            Admin superAdmin = new Admin();
            superAdmin.setName("Super Admin");
            superAdmin.setEmail("admin@justorder.com");
            superAdmin.setPassword(passwordEncoder.encode("admin123"));
            adminRepository.save(superAdmin);
        }

        // Cuisine categories — saved first so restaurants can reference them
        if (cuisineCategoryRepository.count() == 0) {
            cuisineCategoryRepository.save(new CuisineCategory("Italian"));
            cuisineCategoryRepository.save(new CuisineCategory("Chinese"));
            cuisineCategoryRepository.save(new CuisineCategory("Mexican"));
            cuisineCategoryRepository.save(new CuisineCategory("Indian"));
            cuisineCategoryRepository.save(new CuisineCategory("Japanese"));
            cuisineCategoryRepository.save(new CuisineCategory("Mediterranean"));
        }

        // Allergens
        if (alergenRepository.count() == 0) {
            alergenRepository.save(new Alergen("Gluten"));
            alergenRepository.save(new Alergen("Lactose"));
            alergenRepository.save(new Alergen("Peanuts"));
            alergenRepository.save(new Alergen("Shellfish"));
            alergenRepository.save(new Alergen("Soy"));
            alergenRepository.save(new Alergen("Eggs"));
        }

        // Order statuses
        if (orderStatusRepository.count() == 0) {
            orderStatusRepository.save(new OrderStatus("Pending"));
            orderStatusRepository.save(new OrderStatus("Confirmed"));
            orderStatusRepository.save(new OrderStatus("Preparing"));
            orderStatusRepository.save(new OrderStatus("Out for Delivery"));
            orderStatusRepository.save(new OrderStatus("Delivered"));
            orderStatusRepository.save(new OrderStatus("Cancelled"));
        }

        // Test restaurants (only seeded if none exist yet)
        // These cover different cuisines, ratings and price ranges so all CA2 filters can be tested
        if (restaurantRepository.count() == 0) {

            CuisineCategory italian       = cuisineCategoryRepository.findByName("Italian").orElseThrow();
            CuisineCategory japanese      = cuisineCategoryRepository.findByName("Japanese").orElseThrow();
            CuisineCategory mexican       = cuisineCategoryRepository.findByName("Mexican").orElseThrow();
            CuisineCategory mediterranean = cuisineCategoryRepository.findByName("Mediterranean").orElseThrow();

            // --- Restaurant 1: Italian, high rating, mid price ---
            Restaurant pizzaRoma = new Restaurant(
                "Pizza Roma", "Authentic Italian pizza and pasta",
                "+34 600 111 222", "pizzaroma@test.com", "test123",
                "09:00-22:00", "09:00-22:00", "09:00-22:00",
                "09:00-22:00", "09:00-23:00", "10:00-23:00", null
            );
            pizzaRoma.setAverageRating(4.5);
            pizzaRoma.setCuisineCategories(List.of(italian));

            Dish margherita = new Dish("Margherita", "Classic tomato and mozzarella", 9.50, pizzaRoma);
            Dish carbonara  = new Dish("Carbonara", "Creamy pasta with bacon", 12.00, pizzaRoma);
            pizzaRoma.setDishes(List.of(margherita, carbonara));
            restaurantRepository.save(pizzaRoma);

            // --- Restaurant 2: Japanese, very high rating, higher price ---
            Restaurant sushiTokyo = new Restaurant(
                "Sushi Tokyo", "Premium Japanese sushi and ramen",
                "+34 600 333 444", "sushitokyo@test.com", "test123",
                "12:00-22:00", "12:00-22:00", "12:00-22:00",
                "12:00-22:00", "12:00-23:00", "12:00-23:00", "13:00-21:00"
            );
            sushiTokyo.setAverageRating(4.8);
            sushiTokyo.setCuisineCategories(List.of(japanese));

            Dish salmonRoll = new Dish("Salmon Roll (8 pcs)", "Fresh salmon with avocado", 14.00, sushiTokyo);
            Dish ramen      = new Dish("Tonkotsu Ramen", "Rich pork broth ramen", 13.50, sushiTokyo);
            sushiTokyo.setDishes(List.of(salmonRoll, ramen));
            restaurantRepository.save(sushiTokyo);

            // --- Restaurant 3: Mexican, low rating, low price ---
            Restaurant tacoLoco = new Restaurant(
                "Taco Loco", "Street-style Mexican tacos and burritos",
                "+34 600 555 666", "tacoloco@test.com", "test123",
                "11:00-23:00", "11:00-23:00", "11:00-23:00",
                "11:00-23:00", "11:00-00:00", "11:00-00:00", null
            );
            tacoLoco.setAverageRating(3.2);
            tacoLoco.setCuisineCategories(List.of(mexican));

            Dish taco   = new Dish("Beef Taco", "Spicy beef with salsa and guacamole", 4.50, tacoLoco);
            Dish burrito = new Dish("Chicken Burrito", "Grilled chicken with rice and beans", 7.00, tacoLoco);
            tacoLoco.setDishes(List.of(taco, burrito));
            restaurantRepository.save(tacoLoco);

            // --- Restaurant 4: Mediterranean, good rating, mid price ---
            Restaurant oliveGarden = new Restaurant(
                "Olive Garden", "Fresh Mediterranean cuisine",
                "+34 600 777 888", "olivegarden@test.com", "test123",
                "10:00-21:00", "10:00-21:00", "10:00-21:00",
                "10:00-21:00", "10:00-22:00", "10:00-22:00", null
            );
            oliveGarden.setAverageRating(4.1);
            oliveGarden.setCuisineCategories(List.of(mediterranean));

            Dish hummus = new Dish("Hummus Plate", "Homemade hummus with pita bread", 6.50, oliveGarden);
            Dish falafel = new Dish("Falafel Wrap", "Crispy falafel with tahini sauce", 8.00, oliveGarden);
            oliveGarden.setDishes(List.of(hummus, falafel));
            restaurantRepository.save(oliveGarden);
        }
    }
}