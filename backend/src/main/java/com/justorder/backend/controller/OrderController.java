package com.justorder.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.model.Order;
import com.justorder.backend.model.Dish;
import com.justorder.backend.repository.OrderRepository;
import com.justorder.backend.repository.CustomerRepository;
import com.justorder.backend.repository.RiderRepository;
import com.justorder.backend.repository.OrderStatusRepository;
import com.justorder.backend.repository.DishRepository;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired private OrderRepository orderRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private RiderRepository riderRepository;
    @Autowired private OrderStatusRepository orderStatusRepository;
    @Autowired private DishRepository dishRepository;

    private void aplicarCortafuegos(Order order) {
        if (order.getCustomer() != null) {
            order.getCustomer().setOrders(null);
        }
        if (order.getRider() != null) {
            order.getRider().setOrders(null);
        }
        if (order.getDishes() != null) {
            for (Dish dish : order.getDishes()) {
                if (dish.getRestaurant() != null) {
                    dish.getRestaurant().setDishes(null);
                }
            }
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            aplicarCortafuegos(order);
        }
        return ResponseEntity.ok(orders);
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
        if (request.getDishIds() != null && !request.getDishIds().isEmpty()) {
            newOrder.setDishes(dishRepository.findAllById(request.getDishIds()));
        }

        Order savedOrder = orderRepository.save(newOrder);
        aplicarCortafuegos(savedOrder);
        return ResponseEntity.ok(savedOrder);
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
            if (request.getDishIds() != null) {
                existingOrder.setDishes(dishRepository.findAllById(request.getDishIds()));
            }

            Order updatedOrder = orderRepository.save(existingOrder);
            aplicarCortafuegos(updatedOrder);
            return ResponseEntity.ok(updatedOrder);
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