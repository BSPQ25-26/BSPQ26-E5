package com.justorder.backend.repository;

import com.justorder.backend.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {

    Optional<OrderStatus> findByStatus(String status);

    Optional<OrderStatus> findByStatusIgnoreCase(String status);
}