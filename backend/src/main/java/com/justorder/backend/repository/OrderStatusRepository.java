package com.justorder.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.justorder.backend.model.OrderStatus;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {
    
}