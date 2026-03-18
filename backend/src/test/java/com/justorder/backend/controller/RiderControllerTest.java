package com.justorder.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class RiderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

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
            // Create test orders explicitly
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
            
            // Create orders if they don't exist
            if (orderRepository.count() < 2) {
                Order order1 = new Order(customer1, List.of(), pending, rider1, 23.0, "1234");
                Order order2 = new Order(customer1, List.of(), pending, rider1, 14.0, "5678");
                orderRepository.save(order1);
                orderRepository.save(order2);
            }
            
            // Get the actual order IDs
            List<Long> orderIds = orderRepository.findAll().stream()
                .map(Order::getId)
                .sorted()
                .toList();
            
            if (orderIds.size() < 2) {
                throw new IllegalStateException("Failed to create orders for testing");
            }
            
            orderId1 = orderIds.get(0);
            orderId2 = orderIds.get(1);
        }
    }

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
               .andExpect(status().isOk());
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

    /**
     * GET /api/riders/{riderId}/orders → 200 OK with rider's orders.
     * Verifies that a rider can retrieve their assigned orders.
     */
    @Test
    void testGetRiderOrders() throws Exception {
        mockMvc.perform(get("/api/riders/1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    /**
     * GET /api/riders/999/orders → 404 when rider does not exist.
     */
    @Test
    void testGetRiderOrdersRiderNotFound() throws Exception {
        mockMvc.perform(get("/api/riders/999/orders"))
                .andExpect(status().isNotFound());
    }

    /**
     * First rejection of an order → 200 OK, order reassigned to Rider 2.
     */
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

    /**
     * Second rejection of the same order → 200 OK, order cancelled.
     * Verifies that when the reassigned rider also rejects, the order
     * is cancelled (automatic refund) rather than reassigned again.
     */
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

    /**
     * Rider tries to reject an order belonging to a different rider → 403 Forbidden.
     * Verifies the ownership check prevents unauthorized rejection.
     */
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

    /**
     * Rider tries to reject an order that does not exist → 404 Not Found.
     */
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

    /**
     * Rider sends rejection with empty reason → 400 Bad Request.
     * Verifies that an empty reason is rejected before any business logic runs.
     */
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
}