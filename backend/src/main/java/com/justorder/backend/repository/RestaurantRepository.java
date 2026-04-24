package com.justorder.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.justorder.backend.model.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {


    boolean existsByEmail(String email);
    Optional<Restaurant> findByEmail(String email);

    @Query("""
        SELECT DISTINCT r FROM Restaurant r
        LEFT JOIN r.cuisineCategories cc
        LEFT JOIN r.dishes d
        WHERE
            (:cuisineName IS NULL OR LOWER(cc.name) = LOWER(CAST(:cuisineName AS String)))
            AND (:minRating IS NULL OR r.averageRating >= :minRating)
            AND (:minPrice IS NULL OR d.price >= :minPrice)
            AND (:maxPrice IS NULL OR d.price <= :maxPrice)
    """)
    List<Restaurant> findWithFilters(
        @Param("cuisineName") String cuisineName,
        @Param("minRating")   Double minRating,
        @Param("minPrice")    Double minPrice,
        @Param("maxPrice")    Double maxPrice
    );
}