package com.justorder.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.justorder.backend.model.Rider;

public interface RiderRepository extends JpaRepository<Rider, Long> {
    boolean existsByDni(String dni);
	boolean existsByEmail(String email);
    Rider findByEmail(String email);
}