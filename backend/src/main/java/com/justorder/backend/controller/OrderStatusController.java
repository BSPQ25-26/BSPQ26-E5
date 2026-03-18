package com.justorder.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.justorder.backend.dto.OrderStatusDTO;
import com.justorder.backend.model.OrderStatus;
import com.justorder.backend.repository.OrderStatusRepository;

/**
 * REST controller for managing Order Status entities.
 * Provides endpoints for performing CRUD (Create, Read, Update, Delete) 
 * operations on the different statuses an order can have within the system.
 * * @version 1.0
 */
@RestController
@RequestMapping("/api/order-statuses")
public class OrderStatusController {

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    /**
     * Retrieves a list of all available order statuses in the database.
     * * @return a ResponseEntity containing a list of {@link OrderStatus} objects and an HTTP 200 OK status.
     */
    @GetMapping("/all")
    public ResponseEntity<List<OrderStatus>> getAllOrderStatuses() {
        return ResponseEntity.ok(orderStatusRepository.findAll());
    }

    /**
     * Creates a new order status and saves it to the database.
     * Note: Adapts the 'name' field from the DTO to the 'status' field of the Model entity.
     * * @param request the data transfer object containing the details of the order status to be created.
     * @return a ResponseEntity containing the newly created {@link OrderStatus} and an HTTP 200 OK status.
     */
    @PostMapping("/create")
    public ResponseEntity<OrderStatus> createOrderStatus(@RequestBody OrderStatusDTO request) {
        OrderStatus newStatus = new OrderStatus();
        // Adapting the 'name' from the DTO to the 'status' field in the Model
        newStatus.setStatus(request.getName());
        return ResponseEntity.ok(orderStatusRepository.save(newStatus));
    }

    /**
     * Updates an existing order status identified by its ID.
     * * @param id the unique identifier of the order status to be updated.
     * @param request the data transfer object containing the updated name.
     * @return a ResponseEntity containing the updated {@link OrderStatus} if found, 
     * or an HTTP 404 Not Found status if the status does not exist.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<OrderStatus> updateOrderStatus(@PathVariable Long id, @RequestBody OrderStatusDTO request) {
        return orderStatusRepository.findById(id)
            .map(existingStatus -> {
                existingStatus.setStatus(request.getName());
                return ResponseEntity.ok(orderStatusRepository.save(existingStatus));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes a specific order status by its ID.
     * * @param id the unique identifier of the order status to be deleted.
     * @return a ResponseEntity with an HTTP 200 OK status if the deletion was successful, 
     * or an HTTP 404 Not Found status if the order status does not exist.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrderStatus(@PathVariable Long id) {
        if (orderStatusRepository.existsById(id)) {
            orderStatusRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}