package com.justorder.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.justorder.backend.model.Allergen;

public interface AllergenRepository extends JpaRepository<Allergen, Long> {
    // AllergenDTO does not have an id, so we need to find the allergen by its name
    Optional<Allergen> findByName(String name);
}