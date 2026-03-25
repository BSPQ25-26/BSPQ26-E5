package com.justorder.backend.repository;

import com.justorder.backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Finds all orders assigned to a specific rider.
     * Required by RiderService to manage delivery workflows.
     */
    List<Order> findByRiderId(Long riderId);

    /**
     * Finds all orders placed by a specific customer.
     */
    List<Order> findByCustomerId(Long customerId);
}