package com.justorder.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justorder.backend.dto.CheckoutOrderRequestDTO;
import com.justorder.backend.model.Customer;
import com.justorder.backend.model.Dish;
import com.justorder.backend.model.Localization;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.model.Rider;
import com.justorder.backend.repository.CustomerRepository;
import com.justorder.backend.repository.DishRepository;
import com.justorder.backend.repository.RiderRepository;
import com.justorder.backend.repository.RestaurantRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private DishRepository dishRepository;

    @Test
    @Transactional
    public void checkoutCreatesOrder() throws Exception {
        Customer customer = new Customer(
            "Checkout User",
            "checkout.user@justorder.com",
            "+34 600 000 000",
            "plain-pass",
            23,
            "00000000A",
            List.of(),
            List.of(),
            List.of()
        );
        customer = customerRepository.save(customer);

        Localization riderStart = new Localization(
            "Bilbao",
            "Bizkaia",
            "Spain",
            "48001",
            "1",
            -2.934985,
            43.263012
        );
        Rider rider = new Rider(
            "Default Rider",
            "11111111B",
            "+34 611 111 111",
            "rider.checkout@justorder.com",
            "plain-pass",
            riderStart
        );
        riderRepository.save(rider);

        Restaurant restaurant = new Restaurant(
            "Checkout Restaurant",
            "Test restaurant",
            "+34 900 000 100",
            "checkout.restaurant@justorder.com",
            "plain-pass",
            "09:00-22:00",
            "09:00-22:00",
            "09:00-22:00",
            "09:00-22:00",
            "09:00-22:00",
            "09:00-22:00",
            "09:00-22:00"
        );
        restaurant = restaurantRepository.save(restaurant);

        Dish dish = new Dish("Checkout Dish", "Dish for checkout", 18.5, restaurant);
        dish = dishRepository.save(dish);

        CheckoutOrderRequestDTO request = new CheckoutOrderRequestDTO();
        request.setCustomerId(customer.getId());
        request.setDishIds(List.of(dish.getId()));
        request.setClientTotal(18.5);
        request.setPaymentToken("mock-payment-token");

        mockMvc.perform(post("/api/orders/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    public void checkoutCreatesOrderWithRepeatedDishIds() throws Exception {
        Customer customer = new Customer(
            "Repeated Dish User",
            "repeat.user@justorder.com",
            "+34 600 000 001",
            "plain-pass",
            24,
            "00000001A",
            List.of(),
            List.of(),
            List.of()
        );
        customer = customerRepository.save(customer);

        Localization riderStart = new Localization(
            "Bilbao",
            "Bizkaia",
            "Spain",
            "48001",
            "2",
            -2.934985,
            43.263012
        );
        Rider rider = new Rider(
            "Repeated Dish Rider",
            "11111112B",
            "+34 611 111 112",
            "repeat.rider@justorder.com",
            "plain-pass",
            riderStart
        );
        riderRepository.save(rider);

        Restaurant restaurant = new Restaurant(
            "Repeated Dish Restaurant",
            "Test restaurant",
            "+34 900 000 101",
            "repeat.restaurant@justorder.com",
            "plain-pass",
            "09:00-22:00",
            "09:00-22:00",
            "09:00-22:00",
            "09:00-22:00",
            "09:00-22:00",
            "09:00-22:00",
            "09:00-22:00"
        );
        restaurant = restaurantRepository.save(restaurant);

        Dish dish = new Dish("Repeated Checkout Dish", "Dish for repeated quantity", 18.5, restaurant);
        dish = dishRepository.save(dish);

        CheckoutOrderRequestDTO request = new CheckoutOrderRequestDTO();
        request.setCustomerId(customer.getId());
        request.setDishIds(List.of(dish.getId(), dish.getId()));
        request.setClientTotal(37.0);
        request.setPaymentToken("mock-payment-token");

        mockMvc.perform(post("/api/orders/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
