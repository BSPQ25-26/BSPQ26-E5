package com.justorder.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.justorder.backend.model.Dish;

public interface DishRepository extends JpaRepository<Dish, Long> {
}