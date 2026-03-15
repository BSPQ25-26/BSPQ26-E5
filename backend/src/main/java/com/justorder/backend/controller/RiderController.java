package com.justorder.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.justorder.backend.dto.RiderDTO;
import com.justorder.backend.model.Rider;
import com.justorder.backend.model.Order;
import com.justorder.backend.model.Localization;
import com.justorder.backend.repository.RiderRepository;

@RestController
@RequestMapping("/api/riders")
public class RiderController {
    
    @Autowired
    private RiderRepository riderRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Rider>> getAllRiders() {
        List<Rider> riders = riderRepository.findAll();
        
        for (Rider rider : riders) {
            if (rider.getOrders() != null) {
                for (Order order : rider.getOrders()) {
                    order.setRider(null);
                }
            }
        }
        
        return ResponseEntity.ok(riders);
    }

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
        
        if (savedRider.getOrders() != null) {
            for (Order order : savedRider.getOrders()) {
                order.setRider(null);
            }
        }
        
        return ResponseEntity.ok(savedRider);
    }

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
                
                if (updatedRider.getOrders() != null) {
                    for (Order order : updatedRider.getOrders()) {
                        order.setRider(null);
                    }
                }
                
                return ResponseEntity.ok(updatedRider);
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

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