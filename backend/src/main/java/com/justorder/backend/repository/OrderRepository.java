package com.justorder.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.justorder.backend.model.Order;

/**
 * Repository for CRUD operations on {@link Order} entities.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
}
