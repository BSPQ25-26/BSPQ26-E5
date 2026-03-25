package com.justorder.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.justorder.backend.model.CuisineCategory;

@Repository
public interface CuisineCategoryRepository extends JpaRepository<CuisineCategory, Long> {

    /**
     * Finds a cuisine category by its name.
     * Required by DataInitializer to link categories to restaurants.
     */
    Optional<CuisineCategory> findByName(String name);
}