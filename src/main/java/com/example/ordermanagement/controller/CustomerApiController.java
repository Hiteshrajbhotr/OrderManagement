package com.example.ordermanagement.controller;

import com.example.ordermanagement.dto.CustomerRequest;
import com.example.ordermanagement.dto.CustomerResponse;
import com.example.ordermanagement.service.CustomerServiceInterface;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerApiController {

    @Autowired
    private CustomerServiceInterface customerService;

    @GetMapping("/health")
    public String health() {
        return "Customer Management API is running!";
    }

    // ==================== CUSTOMER MANAGEMENT CRUD OPERATIONS ====================

    // CREATE - Add a new customer
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        try {
            CustomerResponse response = customerService.createCustomer(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // READ - Get all customers
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        List<CustomerResponse> customers = customerService.getAllCustomers();
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    // READ - Get customer by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        try {
            CustomerResponse customer = customerService.getCustomerById(id);
            return new ResponseEntity<>(customer, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // UPDATE - Update customer by ID
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Long id, 
                                                        @Valid @RequestBody CustomerRequest request) {
        try {
            CustomerResponse updatedCustomer = customerService.updateCustomer(id, request);
            return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // DELETE - Delete customer by ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
            return new ResponseEntity<>("Customer deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Customer not found", HttpStatus.NOT_FOUND);
        }
    }

    // ==================== ADDITIONAL SEARCH OPERATIONS ====================

    // Search customers by name
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public ResponseEntity<List<CustomerResponse>> searchCustomersByName(@RequestParam String name) {
        List<CustomerResponse> customers = customerService.searchCustomersByName(name);
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    // Get customers by city
    @GetMapping("/city/{city}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public ResponseEntity<List<CustomerResponse>> getCustomersByCity(@PathVariable String city) {
        List<CustomerResponse> customers = customerService.getCustomersByCity(city);
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    // Get customers by state
    @GetMapping("/state/{state}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public ResponseEntity<List<CustomerResponse>> getCustomersByState(@PathVariable String state) {
        List<CustomerResponse> customers = customerService.getCustomersByState(state);
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    // Get customers by country
    @GetMapping("/country/{country}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public ResponseEntity<List<CustomerResponse>> getCustomersByCountry(@PathVariable String country) {
        List<CustomerResponse> customers = customerService.getCustomersByCountry(country);
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    // Get customer by email
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public ResponseEntity<CustomerResponse> getCustomerByEmail(@PathVariable String email) {
        try {
            CustomerResponse customer = customerService.getCustomerByEmail(email);
            return new ResponseEntity<>(customer, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
