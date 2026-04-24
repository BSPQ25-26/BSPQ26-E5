package com.justorder.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justorder.backend.dto.RiderDTO;
import com.justorder.backend.model.Customer;
import com.justorder.backend.model.Localization;
import com.justorder.backend.model.Order;
import com.justorder.backend.model.OrderStatus;
import com.justorder.backend.model.Rider;
import com.justorder.backend.repository.CustomerRepository;
import com.justorder.backend.repository.OrderRepository;
import com.justorder.backend.repository.OrderStatusRepository;
import com.justorder.backend.repository.RiderRepository;
import com.justorder.backend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class RiderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtUtil jwtUtil;

    private Long orderId1;
    private Long orderId2;

    @BeforeEach
    void setUp() {
        // Initialization for tests that don't need order IDs
        // Order IDs are loaded on-demand by ensureOrdersLoaded()
    }

    private void ensureOrdersLoaded() {
        if (orderId1 == null || orderId2 == null) {
            Rider rider1 = riderRepository.findById(1L)
                .orElseGet(() -> {
                    Localization loc = new Localization("Test", "Test", "Test", "28001", "1", 0.0, 0.0);
                    Rider r = new Rider("Test Rider", "12345678A", "+34 612345678", 
                        "test@test.com", "TestPass123", loc);
                    return riderRepository.save(r);
                });
            
            Customer customer1 = customerRepository.findById(1L)
                .orElseGet(() -> {
                    Localization loc = new Localization("Test", "Test", "Test", "28001", "1", 0.0, 0.0);
                    Customer c = new Customer("Test Customer", "test@test.com", "+34 612345678", 
                        "TestPass123", 30, "11111111A", List.of(loc), List.of(), List.of());
                    return customerRepository.save(c);
                });
            
            OrderStatus pending = orderStatusRepository.findByStatus("Pending")
                .orElseGet(() -> orderStatusRepository.save(new OrderStatus("Pending")));

            Order order1 = new Order(customer1, List.of(), pending, rider1, 23.0, passwordEncoder.encode("123456"));
            Order order2 = new Order(customer1, List.of(), pending, rider1, 14.0, passwordEncoder.encode("654321"));
            order1 = orderRepository.save(order1);
            order2 = orderRepository.save(order2);

            orderId1 = order1.getId();
            orderId2 = order2.getId();
        }
    }

    // ==========================================
    // TESTS DE LA RAMA 'HEAD' (ADAPTADOS A DB)
    // ==========================================

    @Test
    void testGetAll() throws Exception {
        Localization loc = new Localization("Bilbao", "Bizkaia", "Spain", "48001", "1", 0.0, 0.0);
        Rider r = new Rider("Carlos Moto", "12345678X", "+34 600000000", "carlos@moto.com", "pass123", loc);
        riderRepository.save(r);

        mockMvc.perform(get("/api/riders"))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Carlos Moto")));
    }

    @Test
    void testUpdate() throws Exception {
        Localization loc = new Localization("Bilbao", "Bizkaia", "Spain", "48001", "1", 0.0, 0.0);
        Rider existing = new Rider("Luis Bici", "12345678Y", "+34 600000001", "luis@bici.com", "pass123", loc);
        existing = riderRepository.save(existing);

        RiderDTO request = new RiderDTO();
        request.setName("Luis Modificado");

        mockMvc.perform(put("/api/riders/" + existing.getId())
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk());
    }

    @Test
    void testDeleteById() throws Exception {
        Localization loc = new Localization("Bilbao", "Bizkaia", "Spain", "48001", "1", 0.0, 0.0);
        Rider existing = new Rider("To Delete", "12345678Z", "+34 600000002", "del@rider.com", "pass123", loc);
        existing = riderRepository.save(existing);

        mockMvc.perform(delete("/api/riders/" + existing.getId()))
               .andExpect(status().isOk());
    }

    // ==========================================
    // TESTS DE LA RAMA 'MAIN' (VALIDACIONES Y LÓGICA DE RECHAZO)
    // ==========================================

    @Test
    void testCreateRider() throws Exception {
        String requestBody = """
                {
                    "name": "Rider Name",
                    "dni": "99999999Z",
                    "phoneNumber": "600123456",
                    "email": "newrider@example.com",
                    "password": "securePasdasdasdasdass123",
                    "starterPoint":{
                        "city": "Bilbao",
                        "province": "Bizkaia",
                        "country": "Spain",
                        "postalCode": "48001",
                        "number": "5",
                        "longitude": -2.9253,
                        "latitude": 43.2630
                    }
                }
                """;
        mockMvc.perform(post("/api/riders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
               .andExpect(status().isCreated());
    }

    @Test
    void testCreateVoidRider() throws Exception {
        String requestBody = """
                {
                }
                """;
        mockMvc.perform(post("/api/riders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
               .andExpect(status().isInternalServerError());
    }

    @Test
    void testNonStartRider() throws Exception {
        String requestBody = """
                {
                    "name": "Rider Name",
                    "dni": "12345678ATest",
                    "phoneNumber": "600123456",
                    "email": "ridasdser@example.com",
                    "password": "securePasdasdasdasdass123"
                }
                """;
        mockMvc.perform(post("/api/riders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
               .andExpect(status().isInternalServerError());
    }

    @Test
    void testDeleteAllRiders() throws Exception {
        mockMvc.perform(delete("/api/riders"))
               .andExpect(status().isOk());
    }

    @Test
    void testGetRiderOrders() throws Exception {
        mockMvc.perform(get("/api/riders/1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetRiderOrdersRiderNotFound() throws Exception {
        mockMvc.perform(get("/api/riders/999/orders"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRejectOrderReassignsToAnotherRider() throws Exception {
        ensureOrdersLoaded();
        String requestBody = """
                {
                    "reason": "Area unreachable due to road closure"
                }
                """;
        mockMvc.perform(post("/api/riders/1/orders/" + orderId1 + "/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rejectionReason").value("Area unreachable due to road closure"))
                .andExpect(jsonPath("$.status").value("Pending"))
                .andExpect(jsonPath("$.riderId").value(2));
    }

    @Test
    void testSecondRejectionCancelsOrder() throws Exception {
        ensureOrdersLoaded();

        mockMvc.perform(post("/api/riders/1/orders/" + orderId2 + "/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "reason": "Vehicle breakdown"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riderId").value(2));

        mockMvc.perform(post("/api/riders/2/orders/" + orderId2 + "/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "reason": "Also unreachable"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Cancelled"))
                .andExpect(jsonPath("$.rejectionReason").value("Also unreachable"));
    }

    @Test
    void testRejectOrderWrongRiderReturnsForbidden() throws Exception {
        ensureOrdersLoaded();
        String requestBody = """
                {
                    "reason": "Wrong rider attempting rejection"
                }
                """;
        mockMvc.perform(post("/api/riders/2/orders/" + orderId1 + "/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRejectOrderNotFoundReturns404() throws Exception {
        String requestBody = """
                {
                    "reason": "Some reason"
                }
                """;
        mockMvc.perform(post("/api/riders/1/orders/999/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRejectOrderWithEmptyReasonReturnsBadRequest() throws Exception {
        ensureOrdersLoaded();
        String requestBody = """
                {
                    "reason": ""
                }
                """;
        mockMvc.perform(post("/api/riders/1/orders/" + orderId1 + "/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testVerifyOrderPinMarksOrderAsDelivered() throws Exception {
        ensureOrdersLoaded();

        mockMvc.perform(post("/api/riders/1/orders/" + orderId1 + "/verify-pin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "pin": "123456"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Delivered"));
    }

    @Test
    void testVerifyOrderPinWrongPinReturnsBadRequest() throws Exception {
        ensureOrdersLoaded();

        mockMvc.perform(post("/api/riders/1/orders/" + orderId1 + "/verify-pin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "pin": "000000"
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testVerifyOrderPinWrongRiderReturnsForbidden() throws Exception {
        ensureOrdersLoaded();

        mockMvc.perform(post("/api/riders/2/orders/" + orderId1 + "/verify-pin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "pin": "123456"
                        }
                        """))
                .andExpect(status().isForbidden());
    }
}