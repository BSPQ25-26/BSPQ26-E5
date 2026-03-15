package com.justorder.backend.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.model.Order;
import com.justorder.backend.repository.OrderRepository;
import com.justorder.backend.repository.CustomerRepository;
import com.justorder.backend.repository.RiderRepository;
import com.justorder.backend.repository.OrderStatusRepository;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RiderRepository riderRepository;
    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody OrderDTO request) {
        Order newOrder = new Order();
        newOrder.setTotalPrice(request.getTotalPrice());
        newOrder.setSecretCode(request.getSecretCode());
        
        if (request.getCustomerId() != null) {
            customerRepository.findById(request.getCustomerId()).ifPresent(newOrder::setCustomer);
        }
        if (request.getRiderId() != null) {
            riderRepository.findById(request.getRiderId()).ifPresent(newOrder::setRider);
        }
        if (request.getStatusId() != null) {
            orderStatusRepository.findById(request.getStatusId()).ifPresent(newOrder::setStatus);
        }
        
        return ResponseEntity.ok(orderRepository.save(newOrder));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody OrderDTO request) {
        return orderRepository.findById(id).map(existingOrder -> {
            existingOrder.setTotalPrice(request.getTotalPrice());
            existingOrder.setSecretCode(request.getSecretCode());
            
            if (request.getCustomerId() != null) {
                customerRepository.findById(request.getCustomerId()).ifPresent(existingOrder::setCustomer);
            }
            if (request.getRiderId() != null) {
                riderRepository.findById(request.getRiderId()).ifPresent(existingOrder::setRider);
            }
            if (request.getStatusId() != null) {
                orderStatusRepository.findById(request.getStatusId()).ifPresent(existingOrder::setStatus);
            }
            
            return ResponseEntity.ok(orderRepository.save(existingOrder));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}