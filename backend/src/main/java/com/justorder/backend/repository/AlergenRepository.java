package com.justorder.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.justorder.backend.model.Alergen;

@Repository
public interface AlergenRepository extends JpaRepository<Alergen, Long> {

    /**
     * Finds an allergen by its name.
     * Required by DataInitializer and AlergenService to handle allergen logic.
     */
    Optional<Alergen> findByName(String name);
}