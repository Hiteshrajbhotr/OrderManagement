package com.example.ordermanagement.repository;

import com.example.ordermanagement.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Find customer by email
    Optional<Customer> findByEmail(String email);

    // Find customers by first name (case-insensitive)
    List<Customer> findByFirstNameContainingIgnoreCase(String firstName);

    // Find customers by last name (case-insensitive)
    List<Customer> findByLastNameContainingIgnoreCase(String lastName);

    // Find customers by city
    List<Customer> findByCity(String city);

    // Find customers by state
    List<Customer> findByState(String state);

    // Find customers by country
    List<Customer> findByCountry(String country);

    // Find customers by pincode
    List<Customer> findByPincode(String pincode);

    // Custom query to find customers by full name
    @Query("SELECT c FROM Customer c WHERE " +
           "LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', :fullName, '%'))")
    List<Customer> findByFullNameContaining(@Param("fullName") String fullName);

    // Check if email exists (for validation)
    boolean existsByEmail(String email);

    // Find customers registered in a specific year
    @Query("SELECT c FROM Customer c WHERE YEAR(c.registrationDate) = :year")
    List<Customer> findByRegistrationYear(@Param("year") int year);
}
