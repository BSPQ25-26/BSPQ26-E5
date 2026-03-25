package com.justorder.backend.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justorder.backend.dto.CheckoutOrderRequestDTO;
import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.model.Customer;
import com.justorder.backend.model.Dish;
import com.justorder.backend.model.Localization;
import com.justorder.backend.model.Order;
import com.justorder.backend.model.OrderStatus;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.model.Rider;
import com.justorder.backend.repository.CustomerRepository;
import com.justorder.backend.repository.DishRepository;
import com.justorder.backend.repository.OrderRepository;
import com.justorder.backend.repository.OrderStatusRepository;
import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.repository.RiderRepository;
import com.justorder.backend.security.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private RiderRepository riderRepository;

    @MockitoBean
    private RestaurantRepository restaurantRepository;

    @MockitoBean
    private DishRepository dishRepository;

    @MockitoBean
    private OrderStatusRepository orderStatusRepository;

    @BeforeEach
    void setUp() {
        OrderStatus pendingStatus = new OrderStatus();
        pendingStatus.setStatus("Pending");
        when(orderStatusRepository.findByStatusIgnoreCase("Pending")).thenReturn(Optional.of(pendingStatus));
        
        Localization riderStart = new Localization("Bilbao", "Bizkaia", "Spain", "48001", "1", -2.934985, 43.263012);
        Rider rider = new Rider("Default Rider", "11111111B", "+34 611 111 111", "rider.checkout@justorder.com", "plain-pass", riderStart);
        rider.setId(20L);
        when(riderRepository.findAll(PageRequest.of(0, 1))).thenReturn(new PageImpl<>(List.of(rider)));
    }

    // ==========================================
    // TESTS DE LA RAMA 'HEAD' (CRUD BÁSICO)
    // ==========================================

    @Test
    public void testGetAll() throws Exception {
        Order o = new Order();
        o.setId(1L);
        o.setSecretCode("CODE-123");
        
        when(orderRepository.findAll()).thenReturn(Arrays.asList(o));

        mockMvc.perform(get("/api/orders"))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("CODE-123")));
    }

    @Test
    public void testCreate() throws Exception {
        OrderDTO request = new OrderDTO();
        request.setSecretCode("CODE-456");

        Order saved = new Order();
        saved.setId(2L);
        saved.setSecretCode("CODE-456");

        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        mockMvc.perform(post("/api/orders")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("CODE-456")));
    }

    @Test
    public void testUpdate() throws Exception {
        OrderDTO request = new OrderDTO();
        request.setSecretCode("CODE-789");

        Order existing = new Order();
        existing.setId(2L);
        existing.setSecretCode("CODE-456");

        Order updated = new Order();
        updated.setId(2L);
        updated.setSecretCode("CODE-789");

        when(orderRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(orderRepository.save(any(Order.class))).thenReturn(updated);

        mockMvc.perform(put("/api/orders/2")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("CODE-789")));
    }

    @Test
    public void testDelete() throws Exception {
        when(orderRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/orders/1"))
               .andExpect(status().isOk());
    }

    // ==========================================
    // TESTS DE LA RAMA 'MAIN' (CHECKOUT)
    // ==========================================

    @Test
    public void checkoutCreatesOrder() throws Exception {
        Customer customer = new Customer("Checkout User", "checkout.user@justorder.com", "+34 600 000 000", "plain-pass", 23, "00000000A", List.of(), List.of(), List.of());
        customer.setId(10L);
        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));

        Restaurant restaurant = new Restaurant();
        restaurant.setId(30L);

        Dish dish = new Dish("Checkout Dish", "Dish for checkout", 18.5, restaurant);
        dish.setId(40L);
        
        when(dishRepository.findAllById(List.of(40L))).thenReturn(List.of(dish));

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

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
    public void checkoutCreatesOrderWithRepeatedDishIds() throws Exception {
        Customer customer = new Customer("Repeated Dish User", "repeat.user@justorder.com", "+34 600 000 001", "plain-pass", 24, "00000001A", List.of(), List.of(), List.of());
        customer.setId(11L);
        when(customerRepository.findById(11L)).thenReturn(Optional.of(customer));

        Dish dish = new Dish("Repeated Checkout Dish", "Dish for repeated quantity", 18.5, new Restaurant());
        dish.setId(41L);
        
        when(dishRepository.findAllById(List.of(41L))).thenReturn(List.of(dish));

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

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