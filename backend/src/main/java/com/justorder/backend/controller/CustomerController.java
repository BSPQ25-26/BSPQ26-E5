package com.justorder.backend.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.justorder.backend.dto.CustomerDashboardDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.justorder.backend.dto.CustomerDTO;
import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.model.Order;
import com.justorder.backend.repository.CustomerRepository;
import com.justorder.backend.repository.OrderRepository;
import com.justorder.backend.service.RegisterService;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

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

    @GetMapping("/hello")
    public String hello() {
        return "Hello from JustOrder!";
    }


    @PostMapping("/create")
    public ResponseEntity<HttpStatus> createOrUpdateCustomer(@RequestBody CustomerDTO request) {
        try {
            this.registerService.registerCustomer(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/order")
    public ResponseEntity<HttpStatus> createOrUpdateOrder(@RequestBody OrderDTO request) { 
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }


    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long customerId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }


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

    @GetMapping("/{customerId}/dashboard")
    public ResponseEntity<CustomerDashboardDTO> getCustomerDashboard(@PathVariable Long customerId) {
    if (!customerRepository.existsById(customerId)) {
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
        .sorted(Comparator.comparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
        .limit(5)
        .map(Order::toDTO)
        .collect(Collectors.toList());

    CustomerDashboardDTO dashboard = new CustomerDashboardDTO(
        customerId,
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


    @DeleteMapping()
    public ResponseEntity<HttpStatus> deleteAllCustomers() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();
        return ResponseEntity.ok(HttpStatus.OK);
    }
}