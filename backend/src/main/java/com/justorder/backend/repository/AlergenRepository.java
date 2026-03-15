package com.justorder.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.justorder.backend.model.Alergen;

public interface AlergenRepository extends JpaRepository<Alergen, Long> {
}