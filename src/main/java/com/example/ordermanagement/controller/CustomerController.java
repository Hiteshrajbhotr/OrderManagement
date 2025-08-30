package com.example.ordermanagement.controller;

import com.example.ordermanagement.dto.CustomerRequest;
import com.example.ordermanagement.dto.CustomerResponse;
import com.example.ordermanagement.service.CustomerServiceInterface;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerServiceInterface customerService;

    // Customers dashboard - list all customers
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public String listCustomers(Model model, @RequestParam(required = false) String search) {
        List<CustomerResponse> customers;
        
        if (search != null && !search.trim().isEmpty()) {
            customers = customerService.searchCustomersByName(search.trim());
            model.addAttribute("searchQuery", search);
        } else {
            customers = customerService.getAllCustomers();
        }
        
        model.addAttribute("customers", customers);
        return "customers/list";
    }

    // Show form to add new customer
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public String showAddForm(Model model) {
        model.addAttribute("customer", new CustomerRequest());
        model.addAttribute("isEdit", false);
        return "customers/form";
    }

    // Process form to add new customer
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public String addCustomer(@Valid @ModelAttribute("customer") CustomerRequest customerRequest,
                           BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "customers/form";
        }
        
        try {
            customerService.createCustomer(customerRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Customer added successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding customer: " + e.getMessage());
        }
        
        return "redirect:/customers";
    }

    // Show form to edit existing customer
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CustomerResponse customer = customerService.getCustomerById(id);
            
            // Convert CustomerResponse to CustomerRequest for form binding
            CustomerRequest customerRequest = new CustomerRequest();
            customerRequest.setFirstName(customer.getFirstName());
            customerRequest.setLastName(customer.getLastName());
            customerRequest.setEmail(customer.getEmail());
            customerRequest.setCity(customer.getCity());
            customerRequest.setState(customer.getState());
            customerRequest.setCountry(customer.getCountry());
            customerRequest.setPincode(customer.getPincode());
            customerRequest.setDateOfBirth(customer.getDateOfBirth());
            customerRequest.setPhoneNumber(customer.getPhoneNumber());
            
            model.addAttribute("customer", customerRequest);
            model.addAttribute("customerId", id);
            model.addAttribute("isEdit", true);
            return "customers/form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Customer not found!");
            return "redirect:/customers";
        }
    }

    // Process form to update existing customer
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public String updateCustomer(@PathVariable Long id,
                              @Valid @ModelAttribute("customer") CustomerRequest customerRequest,
                              BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("customerId", id);
            model.addAttribute("isEdit", true);
            return "customers/form";
        }
        
        try {
            customerService.updateCustomer(id, customerRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Customer updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating customer: " + e.getMessage());
        }
        
        return "redirect:/customers";
    }

    // Delete customer
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("successMessage", "Customer deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting customer: " + e.getMessage());
        }
        
        return "redirect:/customers";
    }

    // View customer details
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public String viewCustomer(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CustomerResponse customer = customerService.getCustomerById(id);
            model.addAttribute("customer", customer);
            return "customers/view";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Customer not found!");
            return "redirect:/customers";
        }
    }

    // Search by city
    @GetMapping("/city/{city}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public String listCustomersByCity(@PathVariable String city, Model model) {
        List<CustomerResponse> customers = customerService.getCustomersByCity(city);
        model.addAttribute("customers", customers);
        model.addAttribute("cityFilter", city);
        return "customers/list";
    }

    // Search by state
    @GetMapping("/state/{state}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public String listCustomersByState(@PathVariable String state, Model model) {
        List<CustomerResponse> customers = customerService.getCustomersByState(state);
        model.addAttribute("customers", customers);
        model.addAttribute("stateFilter", state);
        return "customers/list";
    }

    // Search by country
    @GetMapping("/country/{country}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public String listCustomersByCountry(@PathVariable String country, Model model) {
        List<CustomerResponse> customers = customerService.getCustomersByCountry(country);
        model.addAttribute("customers", customers);
        model.addAttribute("countryFilter", country);
        return "customers/list";
    }

    // View customers grouped by country (for the dashboard button)
    @GetMapping("/country")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public String viewCustomersByCountry(Model model) {
        List<CustomerResponse> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        model.addAttribute("groupByCountry", true);
        return "customers/list";
    }
}
