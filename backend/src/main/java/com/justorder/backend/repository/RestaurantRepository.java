package com.justorder.backend.repository;

import com.justorder.backend.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    /**
     * Checks whether a restaurant with the given email already exists in the database.
     *
     * @param email The email address to check.
     * @return {@code true} if a restaurant with this email exists, {@code false} otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Finds restaurants matching a set of optional filter criteria (CA2 — Browse Restaurants).
     *
     * @param cuisineName Filter by cuisine category name, case-insensitive (e.g. "italian").
     *                    {@code null} = no cuisine filter.
     * @param minRating   Minimum {@code averageRating} inclusive (e.g. 4.0).
     *                    {@code null} = no rating filter.
     * @param minPrice    Minimum dish price in euros inclusive (e.g. 5.0).
     *                    {@code null} = no lower price bound.
     * @param maxPrice    Maximum dish price in euros inclusive (e.g. 20.0).
     *                    {@code null} = no upper price bound.
     * @return Distinct list of restaurants matching all provided filters.
     *         Never {@code null} — returns an empty list if nothing matches.
     */
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