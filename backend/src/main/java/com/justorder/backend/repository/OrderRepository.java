package com.justorder.backend.repository;

import com.justorder.backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByRiderId(Long riderId);
}
