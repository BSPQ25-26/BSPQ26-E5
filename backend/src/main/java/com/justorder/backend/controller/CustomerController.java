package com.justorder.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.justorder.backend.dto.CustomerDTO;
import com.justorder.backend.model.Customer;
import com.justorder.backend.repository.CustomerRepository;

/**
 * REST controller for managing Customer entities.
 * Provides endpoints for performing CRUD (Create, Read, Update, Delete) 
 * operations on customers within the system.
 * * @version 1.0
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Retrieves a list of all available customers in the database.
     * To prevent JSON serialization issues (like infinite recursion) and reduce payload size, 
     * the 'orders' list for each customer is explicitly set to null before returning.
     * * @return a ResponseEntity containing a list of {@link Customer} objects and an HTTP 200 OK status.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        
        for (Customer customer : customers) {
            customer.setOrders(null);
        }
        
        return ResponseEntity.ok(customers);
    }

    /**
     * Creates a new customer and saves it to the database.
     * * @param request the data transfer object containing the details of the customer to be created.
     * @return a ResponseEntity containing the newly created {@link Customer} and an HTTP 200 OK status.
     */
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

    /**
     * Updates an existing customer identified by their ID.
     * If a new password is provided in the request, it will be updated; otherwise, 
     * the existing password remains unchanged.
     * * @param id the unique identifier of the customer to be updated.
     * @param request the data transfer object containing the updated details.
     * @return a ResponseEntity containing the updated {@link Customer} if found, 
     * or an HTTP 404 Not Found status if the customer does not exist.
     */
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

    /**
     * Deletes a specific customer by their ID.
     * * @param id the unique identifier of the customer to be deleted.
     * @return a ResponseEntity with an HTTP 200 OK status if the deletion was successful, 
     * or an HTTP 404 Not Found status if the customer does not exist.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}