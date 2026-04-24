package com.justorder.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.justorder.backend.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByDni(String dni);
	boolean existsByEmail(String email);
    Customer findByEmail(String email);
}