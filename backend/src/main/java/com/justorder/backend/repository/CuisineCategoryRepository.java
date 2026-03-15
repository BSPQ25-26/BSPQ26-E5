package com.justorder.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.justorder.backend.model.CuisineCategory;

public interface CuisineCategoryRepository extends JpaRepository<CuisineCategory, Long> {
}