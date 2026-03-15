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
        List<Customer> customers = customerRepository.findAll();
        
        for (Customer customer : customers) {
            customer.setOrders(null);
        }
        
        return ResponseEntity.ok(customers);
    }

    @PostMapping("/create")
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerDTO request) {
        Customer newCustomer = new Customer();
        newCustomer.setName(request.getName());
        newCustomer.setAge(request.getAge());
        newCustomer.setDni(request.getDni());
        newCustomer.setPhone(request.getPhone());
        newCustomer.setEmail(request.getEmail());
        newCustomer.setPassword(request.getPassword());

        Customer savedCustomer = customerRepository.save(newCustomer);

        savedCustomer.setOrders(null);

        return ResponseEntity.ok(savedCustomer);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO request) {
        return customerRepository.findById(id).map(existingCustomer -> {
            existingCustomer.setName(request.getName());
            existingCustomer.setAge(request.getAge());
            existingCustomer.setDni(request.getDni());
            existingCustomer.setPhone(request.getPhone());
            existingCustomer.setEmail(request.getEmail());
            
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                existingCustomer.setPassword(request.getPassword());
            }

            Customer updatedCustomer = customerRepository.save(existingCustomer);

            updatedCustomer.setOrders(null);

            return ResponseEntity.ok(updatedCustomer);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}