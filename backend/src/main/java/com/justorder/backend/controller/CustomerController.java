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

    public CustomerController(RegisterService registerService,
                              CustomerRepository customerRepository,
                              OrderRepository orderRepository) {
        this.registerService = registerService;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
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
     * @brief Retrieves a customer by ID.
     *
     * @param customerId Unique identifier of the customer.
     * @return HTTP 501 Not Implemented (current placeholder).
     */
    @GetMapping("/{customerId}")
    @Operation(summary = "Get customer by id")
    public ResponseEntity<CustomerDTO> getCustomer(
            @Parameter(description = "Customer id")
            @PathVariable Long customerId) {

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * @brief Retrieves all orders for a given customer.
     *
     * @param customerId Customer identifier.
     * @return List of orders if customer exists, otherwise HTTP 404.
     */
    @GetMapping("/{customerId}/orders")
    @Operation(summary = "Get orders for a customer")
    public ResponseEntity<List<OrderDTO>> getCustomerOrders(
            @Parameter(description = "Customer id")
            @PathVariable Long customerId) {

        logger.info("GET /api/customers/{}/orders - fetching orders", customerId);

        if (!customerRepository.existsById(customerId)) {
            logger.warn("Customer {} not found when requesting orders", customerId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<OrderDTO> orders = orderRepository.findByCustomerId(customerId)
                .stream()
                .map(order -> order.toDTO())
                .collect(Collectors.toList());

        return ResponseEntity.ok(orders);
    }

    /**
     * @brief Retrieves dashboard statistics for a customer.
     *
     * Includes order statistics, spending, refunds, and recent orders.
     *
     * @param customerId Customer identifier.
     * @return Aggregated dashboard data or HTTP 404 if not found.
     */
    @GetMapping("/{customerId}/dashboard")
    @Operation(summary = "Get customer dashboard")
    public ResponseEntity<CustomerDashboardDTO> getCustomerDashboard(
            @Parameter(description = "Customer id")
            @PathVariable Long customerId) {

        logger.info("GET /api/customers/{}/dashboard - dashboard requested", customerId);

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