package com.justorder.backend.repository;

import com.justorder.backend.model.Alergen;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AlergenRepository extends JpaRepository<Alergen, Long> {
    Optional<Alergen> findByName(String name);
}