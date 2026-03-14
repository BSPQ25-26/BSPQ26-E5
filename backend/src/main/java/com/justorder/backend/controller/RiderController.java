package com.justorder.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.justorder.backend.dto.RiderDTO;
import com.justorder.backend.repository.RiderRepository;
import com.justorder.backend.service.RegisterService;

@RestController
@RequestMapping("/api/riders")
public class RiderController {

    private final RegisterService registerService;
    private final RiderRepository riderRepository;

    public RiderController(RegisterService registerService, RiderRepository riderRepository) {
        this.registerService = registerService;
        this.riderRepository = riderRepository;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from JustOrder!";
    }
    
    @PostMapping("/create")
    public ResponseEntity<HttpStatus> createOrUpdateRider(@RequestBody RiderDTO request) {
        //TODO: implement
        try {
            this.registerService.registerRider(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/orders")
    public ResponseEntity<HttpStatus> updateStatus(@RequestBody String request) {
        //TODO: implement
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{riderId}")
    public ResponseEntity<RiderDTO> getRider(@PathVariable String riderId) {
        //TODO: implement
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
    }

    @GetMapping("/{riderId}/orders")
    public ResponseEntity<String> getRiderOrders(@PathVariable String riderId) {
        //TODO: implement
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
    }
    
    @DeleteMapping("/{riderId}/orders/{orderId}")
    public ResponseEntity<String> deleteRiderOrder(@PathVariable String riderId, @PathVariable String orderId) {
        //TODO: implement
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
    }

    @DeleteMapping()
    public ResponseEntity<HttpStatus> deleteAllRiders() {
        riderRepository.deleteAll();
        return ResponseEntity.ok().build();
    }

}
