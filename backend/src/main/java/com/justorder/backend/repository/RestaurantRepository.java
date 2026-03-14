package com.justorder.backend.repository;

import com.justorder.backend.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * <p>Custom queries (like the filter search below) are added as interface
 * methods annotated with {@code @Query} using JPQL (Java Persistence Query
 * Language), which operates on entity class names and field names rather
 * than raw table/column names.</p>
 *
 * @see com.justorder.backend.service.RestaurantService
 * @see com.justorder.backend.model.Restaurant
 */
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    /**
     * Checks whether a restaurant with the given email already exists in the database.
     *
     * <p>Used during registration (IAM-2) to prevent duplicate accounts.
     * Spring Data JPA auto-generates the implementation from the method name (
     * no {@code @Query} annotation needed).</p>
     *
     * @param email The email address to check.
     * @return {@code true} if a restaurant with this email exists, {@code false} otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Finds restaurants matching a set of optional filter criteria (CA2 — Browse Restaurants).
     *
     * <p><b>How the optional filters work:</b><br>
     * Each WHERE condition is written as {@code (:param IS NULL OR actual_condition)}.
     * When a parameter is {@code null}, the {@code :param IS NULL} part is {@code true},
     * which short-circuits the whole condition (effectively skipping that filter).
     * This means one single query handles every possible combination of filters,
     * including no filters at all (returns all restaurants).</p>
     *
     * <p><b>Why {@code DISTINCT}?</b><br>
     * The query joins {@code r.dishes} to filter by price range. If a restaurant
     * has 5 dishes that all fall within the price range, the join produces 5 rows
     * for the same restaurant. {@code DISTINCT} collapses those back into one.</p>
     *
     * <p><b>Why {@code LEFT JOIN}?</b><br>
     * A regular {@code JOIN} would exclude restaurants with no dishes or no cuisine
     * categories. {@code LEFT JOIN} keeps them in the result set, letting the
     * {@code IS NULL} check on the parameter decide whether to filter them out.</p>
     *
     * <p><b>Example calls:</b>
     * <pre>
     *   findWithFilters(null, null, null, null)        // all restaurants
     *   findWithFilters("italian", null, null, null)   // italian only
     *   findWithFilters(null, 4.0, null, null)         // rated 4.0 or above
     *   findWithFilters(null, null, 5.0, 20.0)         // dishes between €5 and €20
     *   findWithFilters("japanese", 4.5, 10.0, 30.0)  // all filters combined
     * </pre>
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