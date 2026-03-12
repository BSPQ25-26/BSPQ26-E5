package com.justorder.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    
    @GetMapping("/create")
    public HttpStatus createCustomer() {
        return HttpStatus.CREATED;
    }
}
