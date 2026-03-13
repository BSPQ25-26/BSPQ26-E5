package com.justorder.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.justorder.backend.dto.CustomerDTO;
import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.service.RegisterService;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final RegisterService registerService;

    public CustomerController(RegisterService registerService) {
        this.registerService = registerService;
    }
    
    @PostMapping("/create")
    public HttpStatus createOrUpdateCustomer(@RequestBody CustomerDTO request) {
        // TODO: implement
        this.registerService.registerCustomer(request);
        return HttpStatus.NOT_IMPLEMENTED;
    }

    @PostMapping("/order")
    public HttpStatus createOrUpdateOrder(@RequestBody OrderDTO request) {
        // TODO: implement
        return HttpStatus.NOT_IMPLEMENTED;
    }
    
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long customerId) {
        // TODO: implement
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{customerId}/orders")
    public ResponseEntity<List<OrderDTO>> getCustomerOrders(@PathVariable Long customerId) {
        // TODO: implement
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
