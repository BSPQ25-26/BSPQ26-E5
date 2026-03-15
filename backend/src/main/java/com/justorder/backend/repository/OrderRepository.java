package com.justorder.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.justorder.backend.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}