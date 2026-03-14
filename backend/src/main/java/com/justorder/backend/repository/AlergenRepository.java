package com.justorder.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.justorder.backend.model.Alergen;

public interface AlergenRepository extends JpaRepository<Alergen, Long> {
    // AlergenDTO does not have an id, so we need to find the alergen by its name
    Optional<Alergen> findByName(String name);
}