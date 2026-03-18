package com.justorder.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.justorder.backend.model.CuisineCategory;

public interface CuisineCategoryRepository extends JpaRepository<CuisineCategory, Long> {
    Optional<CuisineCategory> findByName(String name);
}