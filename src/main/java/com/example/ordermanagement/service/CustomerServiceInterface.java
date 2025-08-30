package com.example.ordermanagement.service;

import com.example.ordermanagement.dto.CustomerRequest;
import com.example.ordermanagement.dto.CustomerResponse;

import java.util.List;

public interface CustomerServiceInterface {
    
    CustomerResponse createCustomer(CustomerRequest request);
    
    CustomerResponse getCustomerById(Long id);
    
    List<CustomerResponse> getAllCustomers();
    
    CustomerResponse updateCustomer(Long id, CustomerRequest request);
    
    void deleteCustomer(Long id);
    
    List<CustomerResponse> searchCustomersByName(String name);
    
    List<CustomerResponse> getCustomersByCity(String city);
    
    List<CustomerResponse> getCustomersByState(String state);
    
    List<CustomerResponse> getCustomersByCountry(String country);
    
    CustomerResponse getCustomerByEmail(String email);
}
