package com.justorder.backend.repository;

import com.justorder.backend.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    /**
     * Checks whether a restaurant with the given email already exists in the database.
     */
    boolean existsByEmail(String email);


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