package com.justorder.backend.repository;

import com.justorder.backend.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {

    /**
     * Finds an OrderStatus by its exact status string.
     * Used in DataInitializer to retrieve status entities like "Pending".
     */
    Optional<OrderStatus> findByStatus(String status);

    /**
     * Finds an OrderStatus by status string, ignoring case sensitivity.
     */
    Optional<OrderStatus> findByStatusIgnoreCase(String status);
}