package com.justorder.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.justorder.backend.model.OrderStatus;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {
	Optional<OrderStatus> findByStatusIgnoreCase(String status);
}