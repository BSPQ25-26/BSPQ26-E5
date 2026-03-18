package com.justorder.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.justorder.backend.dto.RiderDTO;
import com.justorder.backend.model.Rider;
import com.justorder.backend.model.Localization;
import com.justorder.backend.repository.RiderRepository;

/**
 * REST controller for managing Rider (delivery driver) entities.
 * Provides endpoints for performing CRUD (Create, Read, Update, Delete) 
 * operations on riders within the system.
 * * @version 1.0
 */
@RestController
@RequestMapping("/api/riders")
public class RiderController {
    
    @Autowired
    private RiderRepository riderRepository;

    /**
     * Retrieves a list of all available riders in the database.
     * To prevent JSON serialization issues (like infinite recursion), the 'orders' 
     * list for each associated rider is explicitly set to null before returning.
     * * @return a ResponseEntity containing a list of {@link Rider} objects and an HTTP 200 OK status.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Rider>> getAllRiders() {
        List<Rider> riders = riderRepository.findAll();
        
        for (Rider rider : riders) {
            rider.setOrders(null);
        }
        
        return ResponseEntity.ok(riders);
    }

    /**
     * Creates a new rider and saves it to the database.
     * Automatically assigns a default starting point (Localization) to the newly created rider.
     * * @param request the data transfer object containing the details of the rider to be created.
     * @return a ResponseEntity containing the newly created {@link Rider} and an HTTP 200 OK status.
     */
    @PostMapping("/create")
    public ResponseEntity<Rider> createRider(@RequestBody RiderDTO request) {
        Rider newRider = new Rider();
        newRider.setName(request.getName());
        newRider.setEmail(request.getEmail());
        newRider.setPhoneNumber(request.getPhoneNumber());
        newRider.setPassword(request.getPassword());
        
        Localization starter = new Localization();
        starter.setCity("Punto de inicio por defecto");
        newRider.setStarterPoint(starter);

        Rider savedRider = riderRepository.save(newRider);

        savedRider.setOrders(null);
        
        return ResponseEntity.ok(savedRider);
    }

    /**
     * Updates an existing rider identified by their ID.
     * The password field is only updated if a new, non-empty value is provided in the request.
     * * @param id the unique identifier of the rider to be updated.
     * @param request the data transfer object containing the updated details.
     * @return a ResponseEntity containing the updated {@link Rider} if found, 
     * or an HTTP 404 Not Found status if the rider does not exist.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Rider> updateRider(@PathVariable Long id, @RequestBody RiderDTO request) {
        return riderRepository.findById(id)
            .map(existingRider -> {
                existingRider.setName(request.getName());
                existingRider.setEmail(request.getEmail());
                existingRider.setPhoneNumber(request.getPhoneNumber());
                
                if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                    existingRider.setPassword(request.getPassword());
                }
                
                Rider updatedRider = riderRepository.save(existingRider);
                
                updatedRider.setOrders(null);
                
                return ResponseEntity.ok(updatedRider);
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes a specific rider by their ID.
     * * @param id the unique identifier of the rider to be deleted.
     * @return a ResponseEntity with an HTTP 200 OK status if the deletion was successful, 
     * or an HTTP 404 Not Found status if the rider does not exist.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRider(@PathVariable Long id) {
        if (riderRepository.existsById(id)) {
            riderRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}