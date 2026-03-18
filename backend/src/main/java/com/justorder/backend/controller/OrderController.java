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

/**
 * REST controller for managing Order entities.
 * Provides endpoints for performing CRUD operations on orders and handles 
 * complex associations with customers, riders, order statuses, and dishes.
 * * @version 1.0
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired private OrderRepository orderRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private RiderRepository riderRepository;
    @Autowired private OrderStatusRepository orderStatusRepository;
    @Autowired private DishRepository dishRepository;

    /**
     * Helper method that acts as a "firewall" to prevent infinite recursion 
     * during JSON serialization. It nullifies the back-references from associated 
     * entities (like customers, riders, and restaurants inside dishes) to their order lists.
     * * @param order the {@link Order} entity to be sanitized before returning it to the client.
     */
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

    /**
     * Retrieves a list of all available orders in the database.
     * The serialization firewall is applied to each order to keep the JSON response clean and optimized.
     * * @return a ResponseEntity containing a list of {@link Order} objects and an HTTP 200 OK status.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            aplicarCortafuegos(order);
        }
        return ResponseEntity.ok(orders);
    }

    /**
     * Creates a new order and saves it to the database.
     * Links the associated customer, rider, status, and dishes based on the IDs 
     * provided in the request payload.
     * * @param request the data transfer object containing the details of the order to be created.
     * @return a ResponseEntity containing the newly created {@link Order} and an HTTP 200 OK status.
     */
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

    /**
     * Updates an existing order identified by its ID.
     * Re-links all provided associations (customer, rider, status, and dishes) 
     * if they are present in the request DTO.
     * * @param id the unique identifier of the order to be updated.
     * @param request the data transfer object containing the updated details.
     * @return a ResponseEntity containing the updated {@link Order} if found, 
     * or an HTTP 404 Not Found status if the order does not exist.
     */
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

    /**
     * Deletes a specific order by its ID.
     * * @param id the unique identifier of the order to be deleted.
     * @return a ResponseEntity with an HTTP 200 OK status if the deletion was successful, 
     * or an HTTP 404 Not Found status if the order does not exist.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}