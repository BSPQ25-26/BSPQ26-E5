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
import com.justorder.backend.service.RegisterService;
@RestController
@RequestMapping("/api/riders")
public class RiderController {

    private final RegisterService registerService;

    public RiderController(RegisterService registerService) {
        this.registerService = registerService;
    }
    
    @PostMapping("/create")
    public HttpStatus createOrUpdateRider(@RequestBody RiderDTO request) {
        this.registerService.registerRider(request);
        return HttpStatus.NOT_IMPLEMENTED;
    }

    @PostMapping("/orders")
    public HttpStatus updateStatus(@RequestBody String request) {
        //TODO: implement
        return HttpStatus.NOT_IMPLEMENTED;
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

}
