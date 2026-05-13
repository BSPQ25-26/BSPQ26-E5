package com.justorder.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.justorder.backend.dto.AllergenDTO;
import com.justorder.backend.service.AllergenService;

/**
 * @brief Controller for managing allergen-related operations.
 *
 * This controller exposes endpoints to retrieve allergen data
 * from the system.
 */
@RestController
@RequestMapping("/api/allergens")
@Tag(name = "Allergens")
public class AllergenController {

    @Autowired
    private AllergenService allergenService;

    // GET /api/allergens returns a list containing all the allergens

    /**
     * @brief Retrieves the full list of allergens.
     *
     * This endpoint returns all allergens available in the system.
     *
     * @return ResponseEntity containing a list of AllergenDTO objects.
     *         Returns HTTP 200 if successful, or HTTP 500 in case of error.
     */
    @Operation(summary = "List all allergens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Allergens retrieved"),
        @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping
    public ResponseEntity<List<AllergenDTO>> getAllAllergens() {
        try {
            return ResponseEntity.ok(allergenService.getAllAllergens());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
