package com.justorder.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.justorder.backend.model.Rider;

import java.util.Optional;

public interface RiderRepository extends JpaRepository<Rider, Long> {
    boolean existsByDni(String dni);
	boolean existsByEmail(String email);
    Optional<Rider> findByEmail(String email);
}