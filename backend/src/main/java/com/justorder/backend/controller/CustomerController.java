package com.justorder.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.justorder.backend.dto.CustomerDTO;
import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.model.Customer;
import com.justorder.backend.repository.CustomerRepository;
import com.justorder.backend.repository.OrderRepository;
import com.justorder.backend.service.RegisterService;

/**
 * REST controller for managing Customer entities.
 * Provides endpoints for performing CRUD operations and managing customer orders.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final RegisterService registerService;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    /**
     * Constructor injection for better testability and following Spring recommendations.
     */
    public CustomerController(RegisterService registerService,
                              CustomerRepository customerRepository,
                              OrderRepository orderRepository) {
        this.registerService = registerService;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from JustOrder!";
    }

    /**
     * Retrieves all customers. To avoid recursion issues, we return DTOs.
     */
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        List<CustomerDTO> customers = customerRepository.findAll().stream()
                .map(Customer::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(customers);
    }

    /**
     * Creates a new customer using the RegisterService.
     */
    @PostMapping("/create")
    public ResponseEntity<Void> createCustomer(@RequestBody CustomerDTO request) {
        try {
            this.registerService.registerCustomer(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Updates an existing customer.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO request) {
        return customerRepository.findById(id).map(existing -> {
            existing.setName(request.getName());
            existing.setAge(request.getAge());
            existing.setDni(request.getDni());
            existing.setPhone(request.getPhone());
            existing.setEmail(request.getEmail());
            
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                existing.setPassword(request.getPassword());
            }

            Customer updated = customerRepository.save(existing);
            return ResponseEntity.ok(updated.toDTO());
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Retrieves a specific customer by ID.
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long customerId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * Creates or updates an order for a customer.
     */
    @PostMapping("/order")
    public ResponseEntity<HttpStatus> createOrUpdateOrder(@RequestBody OrderDTO request) { 
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * Retrieves all orders for a specific customer.
     */
    @GetMapping("/{customerId}/orders")
    public ResponseEntity<List<OrderDTO>> getCustomerOrders(@PathVariable Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<OrderDTO> orders = orderRepository.findByCustomerId(customerId)
                .stream()
                .map(order -> order.toDTO())
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    /**
     * Deletes a specific customer by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Deletes all customers and their orders from the database.
     */
    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteAllCustomers() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();
        return ResponseEntity.ok(HttpStatus.OK);
    }
}