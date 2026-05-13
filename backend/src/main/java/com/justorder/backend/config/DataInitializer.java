package com.justorder.backend.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.justorder.backend.model.Admin;
import com.justorder.backend.model.Allergen;
import com.justorder.backend.model.CuisineCategory;
import com.justorder.backend.model.Customer;
import com.justorder.backend.model.Dish;
import com.justorder.backend.model.Localization;
import com.justorder.backend.model.Order;
import com.justorder.backend.model.OrderStatus;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.model.Rider;
import com.justorder.backend.repository.AdminRepository;
import com.justorder.backend.repository.AllergenRepository;
import com.justorder.backend.repository.CuisineCategoryRepository;
import com.justorder.backend.repository.CustomerRepository;
import com.justorder.backend.repository.DishRepository;
import com.justorder.backend.repository.OrderRepository;
import com.justorder.backend.repository.OrderStatusRepository;
import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.repository.RiderRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CuisineCategoryRepository cuisineCategoryRepository;

    @Autowired
    private AllergenRepository allergenRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void run(String... args) throws Exception {

        if (adminRepository.count() == 0) {
            Admin superAdmin = new Admin();
            superAdmin.setName("Super Admin");
            superAdmin.setEmail("admin@justorder.com");
            superAdmin.setPassword(passwordEncoder.encode("admin123"));
            adminRepository.save(superAdmin);
        }

        if (cuisineCategoryRepository.count() == 0) {
            cuisineCategoryRepository.save(new CuisineCategory("Italian"));
            cuisineCategoryRepository.save(new CuisineCategory("Chinese"));
            cuisineCategoryRepository.save(new CuisineCategory("Mexican"));
            cuisineCategoryRepository.save(new CuisineCategory("Indian"));
            cuisineCategoryRepository.save(new CuisineCategory("Japanese"));
            cuisineCategoryRepository.save(new CuisineCategory("Mediterranean"));
        }

        if (allergenRepository.count() == 0) {
            allergenRepository.save(new Allergen("Gluten", "Cereals containing gluten"));
            allergenRepository.save(new Allergen("Lactose", "Milk and dairy products"));
            allergenRepository.save(new Allergen("Peanuts", "Peanuts and peanut-based products"));
            allergenRepository.save(new Allergen("Shellfish", "Crustaceans and shellfish products"));
            allergenRepository.save(new Allergen("Soy", "Soybeans and soy-based products"));
            allergenRepository.save(new Allergen("Eggs", "Eggs and egg-based products"));
        }

        if (orderStatusRepository.count() == 0) {
            orderStatusRepository.save(new OrderStatus("Pending"));
            orderStatusRepository.save(new OrderStatus("Confirmed"));
            orderStatusRepository.save(new OrderStatus("Preparing"));
            orderStatusRepository.save(new OrderStatus("Out for Delivery"));
            orderStatusRepository.save(new OrderStatus("Delivered"));
            orderStatusRepository.save(new OrderStatus("Cancelled"));
        }

        if (restaurantRepository.count() == 0) {

            Restaurant restaurant1 = new Restaurant(
                "La Marina Bistro", "Mediterranean restaurant near the seafront",
                "+34 900 000 001", "lamarina@justorder.com",
                passwordEncoder.encode("restaurant123"),
                "09:00-22:00", "09:00-22:00", "09:00-22:00",
                "09:00-22:00", "09:00-23:00", "10:00-23:00", "10:00-22:00"
            );
            restaurant1 = restaurantRepository.save(restaurant1);

            Restaurant restaurant2 = new Restaurant(
                "Green Bowl Kitchen", "Healthy bowls and plant-forward comfort food",
                "+34 900 000 002", "greenbowl@justorder.com",
                passwordEncoder.encode("restaurant123"),
                "11:00-22:00", "11:00-22:00", "11:00-22:00",
                "11:00-22:00", "11:00-23:00", "11:00-23:00", "11:00-21:00"
            );
            restaurant2 = restaurantRepository.save(restaurant2);

            if (dishRepository.count() == 0) {
                Allergen gluten  = allergenRepository.findByName("Gluten").orElseThrow();
                Allergen lactose = allergenRepository.findByName("Lactose").orElseThrow();
                Allergen soy     = allergenRepository.findByName("Soy").orElseThrow();
                Allergen eggs    = allergenRepository.findByName("Eggs").orElseThrow();
                Allergen peanuts = allergenRepository.findByName("Peanuts").orElseThrow();

                Dish dish1 = new Dish("Four Cheese Pizza", "Stone-baked pizza with four cheeses", 23.0, restaurant1);
                dish1.setAllergens(Arrays.asList(gluten, lactose));
                Dish dish2 = new Dish("Grilled Salmon", "Grilled salmon fillet with herbs", 25.0, restaurant1);
                dish2.setAllergens(List.of());
                Dish dish3 = new Dish("Thai Tofu Bowl", "Tofu bowl with peanut sauce and scrambled egg", 19.5, restaurant2);
                dish3.setAllergens(Arrays.asList(soy, eggs, peanuts));

                dishRepository.save(dish1);
                dishRepository.save(dish2);
                dishRepository.save(dish3);
            }

            CuisineCategory italian       = cuisineCategoryRepository.findByName("Italian").orElseThrow();
            CuisineCategory japanese      = cuisineCategoryRepository.findByName("Japanese").orElseThrow();
            CuisineCategory mexican       = cuisineCategoryRepository.findByName("Mexican").orElseThrow();
            CuisineCategory mediterranean = cuisineCategoryRepository.findByName("Mediterranean").orElseThrow();

            Restaurant pizzaRoma = new Restaurant(
                "Pizza Roma", "Authentic Italian pizza and pasta",
                "+34 600 111 222", "pizzaroma@test.com", "test123",
                "09:00-22:00", "09:00-22:00", "09:00-22:00",
                "09:00-22:00", "09:00-23:00", "10:00-23:00", null
            );
            pizzaRoma.setAverageRating(4.5);
            pizzaRoma.setCuisineCategories(List.of(italian));
            pizzaRoma.setDishes(List.of(
                new Dish("Margherita", "Classic tomato and mozzarella", 9.50, pizzaRoma),
                new Dish("Carbonara", "Creamy pasta with bacon", 12.00, pizzaRoma)
            ));
            restaurantRepository.save(pizzaRoma);

            Restaurant sushiTokyo = new Restaurant(
                "Sushi Tokyo", "Premium Japanese sushi and ramen",
                "+34 600 333 444", "sushitokyo@test.com", "test123",
                "12:00-22:00", "12:00-22:00", "12:00-22:00",
                "12:00-22:00", "12:00-23:00", "12:00-23:00", "13:00-21:00"
            );
            sushiTokyo.setAverageRating(4.8);
            sushiTokyo.setCuisineCategories(List.of(japanese));
            sushiTokyo.setDishes(List.of(
                new Dish("Salmon Roll (8 pcs)", "Fresh salmon with avocado", 14.00, sushiTokyo),
                new Dish("Tonkotsu Ramen", "Rich pork broth ramen", 13.50, sushiTokyo)
            ));
            restaurantRepository.save(sushiTokyo);

            Restaurant tacoLoco = new Restaurant(
                "Taco Loco", "Street-style Mexican tacos and burritos",
                "+34 600 555 666", "tacoloco@test.com", "test123",
                "11:00-23:00", "11:00-23:00", "11:00-23:00",
                "11:00-23:00", "11:00-00:00", "11:00-00:00", null
            );
            tacoLoco.setAverageRating(3.2);
            tacoLoco.setCuisineCategories(List.of(mexican));
            tacoLoco.setDishes(List.of(
                new Dish("Beef Taco", "Spicy beef with salsa and guacamole", 4.50, tacoLoco),
                new Dish("Chicken Burrito", "Grilled chicken with rice and beans", 7.00, tacoLoco)
            ));
            restaurantRepository.save(tacoLoco);

            Restaurant oliveGarden = new Restaurant(
                "Olive Garden", "Fresh Mediterranean cuisine",
                "+34 600 777 888", "olivegarden@test.com", "test123",
                "10:00-21:00", "10:00-21:00", "10:00-21:00",
                "10:00-21:00", "10:00-22:00", "10:00-22:00", null
            );
            oliveGarden.setAverageRating(4.1);
            oliveGarden.setCuisineCategories(List.of(mediterranean));
            oliveGarden.setDishes(List.of(
                new Dish("Hummus Plate", "Homemade hummus with pita bread", 6.50, oliveGarden),
                new Dish("Falafel Wrap", "Crispy falafel with tahini sauce", 8.00, oliveGarden)
            ));
            restaurantRepository.save(oliveGarden);
        }

        if (riderRepository.count() == 0) {
            // Constructor: city, province, country, postalCode, number, longitude, latitude
            Localization rider1Location = new Localization(
                "Bilbao", "Bizkaia", "Spain", "48001", "1", -2.9253, 43.2630
            );
            Rider rider1 = new Rider(
                "Carlos Rider", "12345678A", "+34 611 111 111",
                "carlos.rider@test.com", passwordEncoder.encode("rider123"),
                rider1Location
            );
            riderRepository.save(rider1);

            Localization rider2Location = new Localization(
                "Bilbao", "Bizkaia", "Spain", "48002", "20", -2.9300, 43.2650
            );
            Rider rider2 = new Rider(
                "Ana Rider", "87654321B", "+34 622 222 222",
                "ana.rider@test.com", passwordEncoder.encode("rider123"),
                rider2Location
            );
            riderRepository.save(rider2);
        }

        if (customerRepository.count() == 0) {
            Localization customerLocation = new Localization(
                "Bilbao", "Bizkaia", "Spain", "48003", "5", -2.9200, 43.2600
            );
            Localization customerLocation2 = new Localization(
                "Bilbao", "Bizkaia", "Spain", "48003", "5", -2.9200, 43.2600
            );
            Customer customer1 = new Customer(
                "Test Customer", "customer@test.com", "+34 633 333 333",
                passwordEncoder.encode("customer123"), 30, "11111111C",
                List.of(customerLocation), List.of(), List.of()
            );
            Customer customer2 = new Customer(
                "John Doe", "john.doe@test.com", "+34 644 444 444",
                passwordEncoder.encode("customer123"), 30, "22222222D",
                List.of(customerLocation2), List.of(), List.of()
            );
            customerRepository.save(customer1);
            customerRepository.save(customer2);
        }

        // Orders depend on riders + customers existing, so we check both
        if (orderRepository.count() == 0
                && riderRepository.count() > 0
                && customerRepository.count() > 0) {

            Rider rider1        = riderRepository.findAll().get(0);
            Customer customer1  = customerRepository.findAll().get(0);
            OrderStatus pending = orderStatusRepository.findByStatus("Pending").orElseThrow();
            Dish dish1 = dishRepository.findAll().get(0);

            // Order 1 — for testing rejection + reassignment to Rider 2
            Order order1 = new Order(customer1, List.of(dish1), pending, rider1, 23.0, passwordEncoder.encode("1234"));
            orderRepository.save(order1);

            // Order 2 — for testing assignment (starts unassigned)
            Order order2 = new Order(customer1, List.of(dish1), pending, null, 14.0, passwordEncoder.encode("5678"));
            orderRepository.save(order2);
        }
    }
}