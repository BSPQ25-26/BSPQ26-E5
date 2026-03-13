package com.justorder.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.justorder.backend.dto.CustomerDTO;
import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.repository.CustomerRepository;
import com.justorder.backend.service.RegisterService;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final RegisterService registerService;
    private final CustomerRepository customerRepository;

    public CustomerController(RegisterService registerService, CustomerRepository customerRepository) {
        this.registerService = registerService;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from JustOrder!";
    }
    
    @PostMapping("/create")
    public ResponseEntity<HttpStatus> createOrUpdateCustomer(@RequestBody CustomerDTO request) {
        // TODO: implement
        try {
            this.registerService.registerCustomer(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/order")
    public ResponseEntity<HttpStatus> createOrUpdateOrder(@RequestBody OrderDTO request) {
        // TODO: implement
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
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

    @DeleteMapping()
    public ResponseEntity<HttpStatus> deleteAllCustomers() {
        customerRepository.deleteAll();
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
