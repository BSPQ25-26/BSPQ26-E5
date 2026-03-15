package com.justorder.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.justorder.backend.dto.OrderStatusDTO;
import com.justorder.backend.model.OrderStatus;
import com.justorder.backend.repository.OrderStatusRepository;

@RestController
@RequestMapping("/api/order-statuses")
public class OrderStatusController {

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @GetMapping("/all")
    public ResponseEntity<List<OrderStatus>> getAllOrderStatuses() {
        return ResponseEntity.ok(orderStatusRepository.findAll());
    }

    @PostMapping("/create")
    public ResponseEntity<OrderStatus> createOrderStatus(@RequestBody OrderStatusDTO request) {
        OrderStatus newStatus = new OrderStatus();
        newStatus.setName(request.getName());
        return ResponseEntity.ok(orderStatusRepository.save(newStatus));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<OrderStatus> updateOrderStatus(@PathVariable Long id, @RequestBody OrderStatusDTO request) {
        return orderStatusRepository.findById(id)
            .map(existingStatus -> {
                existingStatus.setName(request.getName());
                return ResponseEntity.ok(orderStatusRepository.save(existingStatus));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

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