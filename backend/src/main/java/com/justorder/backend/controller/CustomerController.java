package com.justorder.backend.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.justorder.backend.dto.CustomerDashboardDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.justorder.backend.dto.CustomerDTO;
import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.model.Customer;
import com.justorder.backend.model.Order;
import com.justorder.backend.repository.CustomerRepository;
import com.justorder.backend.repository.OrderRepository;
import com.justorder.backend.service.RegisterService;
import com.justorder.backend.service.SessionService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @brief Controller for managing customer-related operations.
 *
 * This controller handles customer creation, retrieval, order access,
 * and dashboard analytics.
 */
@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers")
public class CustomerController {

    private static final Logger logger = LogManager.getLogger(CustomerController.class);

    private final RegisterService registerService;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final SessionService sessionService;

    public CustomerController(RegisterService registerService,
                              CustomerRepository customerRepository,
                              OrderRepository orderRepository,
                              SessionService sessionService) {
        this.registerService = registerService;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.sessionService = sessionService;
    }

    /**
     * @brief Simple health check endpoint for customer API.
     *
     * @return A greeting string confirming service availability.
     */
    @GetMapping("/hello")
    @Operation(summary = "Health check for customer endpoints")
    public String hello() {
        return "Hello from JustOrder!";
    }

    /**
     * @brief Creates or updates a customer.
     *
     * Registers a new customer or updates an existing one.
     *
     * @param request Customer data transfer object.
     * @return HTTP 200 if successful, or HTTP 500 on error.
     */
    @PostMapping("/create")
    @Operation(summary = "Create or update a customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer created/updated successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<HttpStatus> createOrUpdateCustomer(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Customer payload"
            )
            @RequestBody CustomerDTO request) {

        try {
            logger.info("POST /api/customers/create - register customer request: {}",
                    request != null ? request.getEmail() : "<null>");

            this.registerService.registerCustomer(request);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error registering customer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * @brief Creates or updates an order (placeholder endpoint).
     *
     * @param request Order data transfer object.
     * @return HTTP 501 Not Implemented.
     */
    @PostMapping("/order")
    @Operation(summary = "Create or update an order (placeholder)")
    public ResponseEntity<HttpStatus> createOrUpdateOrder(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Order payload"
            )
            @RequestBody OrderDTO request) {

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * @brief Retrieves a customer profile.
     *
     * @return HTTP 200 with profile data, HTTP 401 if unauthorized, or HTTP 404 if not found.
     */
    @GetMapping("/profile")
    @Operation(summary = "Get my customer profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerDTO> getCustomerProfile(
            @Parameter(description = "Authorization header: Bearer <token>")
            @RequestHeader("Authorization") String authorization) {

        try {
            Long customerId = sessionService.getActiveCustomerId(authorization);
            logger.info("GET /api/customers/profile - profile request for customer {}", customerId);
            Customer customer = customerRepository.findById(customerId).orElse(null);
            if (customer == null) {
                logger.warn("Customer {} not found for profile", customerId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(customer.toDTO());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * @brief Retrieves all orders for a given customer.
     *
     * @return List of orders if customer exists, otherwise HTTP 404 or 401.
     */
    @GetMapping("/orders")
    @Operation(summary = "Get my orders")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<List<OrderDTO>> getMyOrders(
            @Parameter(description = "Authorization header: Bearer <token>")
            @RequestHeader("Authorization") String authorization) {

        try {
            Long customerId = sessionService.getActiveCustomerId(authorization);
            logger.info("GET /api/customers/orders - fetching orders for customer {}", customerId);

            if (!customerRepository.existsById(customerId)) {
                logger.warn("Customer {} not found when requesting orders", customerId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            List<OrderDTO> orders = orderRepository.findByCustomerId(customerId)
                    .stream()
                    .map(order -> order.toDTO())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(orders);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * @brief Retrieves dashboard statistics for a customer.
     *
     * Includes order statistics, spending, refunds, and recent orders.
     *
     * @return Aggregated dashboard data or HTTP 404/401 if unauthorized.
     */
    @GetMapping("/dashboard")
    @Operation(summary = "Get my dashboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dashboard retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerDashboardDTO> getMyDashboard(
            @Parameter(description = "Authorization header: Bearer <token>")
            @RequestHeader("Authorization") String authorization) {

        try {
            Long customerId = sessionService.getActiveCustomerId(authorization);
            logger.info("GET /api/customers/dashboard - dashboard requested for customer {}", customerId);

            Customer customer = customerRepository.findById(customerId).orElse(null);

        if (customer == null) {
            logger.warn("Customer {} not found for dashboard", customerId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Order> customerOrders = orderRepository.findByCustomerId(customerId);

        long totalOrders = customerOrders.size();
        long cancelledOrders = customerOrders.stream()
                .filter(order -> "Cancelled".equalsIgnoreCase(order.getStatus().getStatus()))
                .count();
        long deliveredOrders = customerOrders.stream()
                .filter(order -> "Delivered".equalsIgnoreCase(order.getStatus().getStatus()))
                .count();
        long activeOrders = totalOrders - cancelledOrders;

        double totalSpent = customerOrders.stream()
                .filter(order -> !"Cancelled".equalsIgnoreCase(order.getStatus().getStatus()))
                .mapToDouble(Order::getTotalPrice)
                .sum();

        double totalRefunded = customerOrders.stream()
                .filter(order -> "Cancelled".equalsIgnoreCase(order.getStatus().getStatus()))
                .mapToDouble(Order::getTotalPrice)
                .sum();

        List<OrderDTO> recentOrders = customerOrders.stream()
                .sorted(Comparator.comparing(
                        Order::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .limit(5)
                .map(Order::toDTO)
                .collect(Collectors.toList());

        CustomerDashboardDTO dashboard = new CustomerDashboardDTO(
                customerId,
                customer.getName(),
                totalOrders,
                activeOrders,
                cancelledOrders,
                deliveredOrders,
                totalSpent,
                totalRefunded,
                recentOrders
        );

        return ResponseEntity.ok(dashboard);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * @brief Deletes all customers and associated orders.
     *
     * WARNING: This operation removes all customer and order data.
     *
     * @return HTTP 200 when deletion is completed.
     */
    @DeleteMapping()
    @Operation(summary = "Delete all customers and their orders")
    public ResponseEntity<HttpStatus> deleteAllCustomers() {

        orderRepository.deleteAll();
        customerRepository.deleteAll();

        return ResponseEntity.ok(HttpStatus.OK);
    }
}