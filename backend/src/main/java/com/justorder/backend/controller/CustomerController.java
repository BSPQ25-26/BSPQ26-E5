package com.justorder.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.justorder.backend.dto.CustomerDTO;
import com.justorder.backend.model.Customer;
import com.justorder.backend.repository.CustomerRepository;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerRepository.findAll());
    }

    @PostMapping("/create")
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerDTO request) {
        Customer newCustomer = new Customer();
        newCustomer.setName(request.getName());
        newCustomer.setEmail(request.getEmail());
        newCustomer.setPhone(request.getPhone());
        newCustomer.setPassword(request.getPassword());
        newCustomer.setAge(request.getAge());
        newCustomer.setDni(request.getDni());
        
        Customer savedCustomer = customerRepository.save(newCustomer);
        return ResponseEntity.ok(savedCustomer);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO request) {
        return customerRepository.findById(id)
            .map(existingCustomer -> {
                existingCustomer.setName(request.getName());
                existingCustomer.setEmail(request.getEmail());
                existingCustomer.setPhone(request.getPhone());
                existingCustomer.setAge(request.getAge());
                existingCustomer.setDni(request.getDni());
                
                if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                    existingCustomer.setPassword(request.getPassword());
                }
                
                return ResponseEntity.ok(customerRepository.save(existingCustomer));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}