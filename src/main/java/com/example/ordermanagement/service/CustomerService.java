package com.example.ordermanagement.service;

import com.example.ordermanagement.dto.CustomerRequest;
import com.example.ordermanagement.dto.CustomerResponse;
import com.example.ordermanagement.model.Customer;
import com.example.ordermanagement.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService implements CustomerServiceInterface {

    @Autowired
    private CustomerRepository customerRepository;

    // Create a new customer
    public CustomerResponse createCustomer(CustomerRequest request) {
        // Check if email already exists
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Customer with email " + request.getEmail() + " already exists");
        }

        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setCity(request.getCity());
        customer.setState(request.getState());
        customer.setCountry(request.getCountry());
        customer.setPincode(request.getPincode());

        Customer savedCustomer = customerRepository.save(customer);
        return convertToResponse(savedCustomer);
    }

    // Get all customers
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get customer by ID
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        return convertToResponse(customer);
    }

    // Update customer
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        // Check if email is being changed and if new email already exists
        if (!existingCustomer.getEmail().equals(request.getEmail()) && 
            customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Customer with email " + request.getEmail() + " already exists");
        }

        existingCustomer.setFirstName(request.getFirstName());
        existingCustomer.setLastName(request.getLastName());
        existingCustomer.setEmail(request.getEmail());
        existingCustomer.setDateOfBirth(request.getDateOfBirth());
        existingCustomer.setPhoneNumber(request.getPhoneNumber());
        existingCustomer.setCity(request.getCity());
        existingCustomer.setState(request.getState());
        existingCustomer.setCountry(request.getCountry());
        existingCustomer.setPincode(request.getPincode());

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return convertToResponse(updatedCustomer);
    }

    // Delete customer
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    // Search customers by name
    public List<CustomerResponse> searchCustomersByName(String name) {
        return customerRepository.findByFullNameContaining(name)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get customers by city
    public List<CustomerResponse> getCustomersByCity(String city) {
        return customerRepository.findByCity(city)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get customers by state
    public List<CustomerResponse> getCustomersByState(String state) {
        return customerRepository.findByState(state)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get customers by country
    public List<CustomerResponse> getCustomersByCountry(String country) {
        return customerRepository.findByCountry(country)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get customer by email
    public CustomerResponse getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));
        return convertToResponse(customer);
    }

    // Helper method to convert Customer entity to CustomerResponse DTO
    private CustomerResponse convertToResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getDateOfBirth(),
                customer.getPhoneNumber(),
                customer.getCity(),
                customer.getState(),
                customer.getCountry(),
                customer.getPincode(),
                customer.getRegistrationDate(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}
