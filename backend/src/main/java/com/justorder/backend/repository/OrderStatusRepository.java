package com.justorder.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.justorder.backend.model.OrderStatus;

/**
 * Repository for order status catalog entities.
 */
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {
	/**
	 * Finds an order status by name without case sensitivity.
	 *
	 * @param status status label to search
	 * @return optional status entity
	 */
	Optional<OrderStatus> findByStatusIgnoreCase(String status);
}