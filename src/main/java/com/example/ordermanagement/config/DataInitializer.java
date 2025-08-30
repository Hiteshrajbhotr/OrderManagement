package com.example.ordermanagement.config;

import com.example.ordermanagement.model.Customer;
import com.example.ordermanagement.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize with sample data if database is empty
        if (customerRepository.count() == 0) {
            initializeSampleData();
        }
    }

    private void initializeSampleData() {
        // Create sample customers
        Customer customer1 = new Customer();
        customer1.setFirstName("Alice");
        customer1.setLastName("Johnson");
        customer1.setEmail("alice.johnson@example.com");
        customer1.setDateOfBirth(LocalDate.of(1999, 5, 15));
        customer1.setPhoneNumber("1234567890");
        customer1.setCity("New York");
        customer1.setState("New York");
        customer1.setCountry("United States");
        customer1.setPincode("10001");

        Customer customer2 = new Customer();
        customer2.setFirstName("Bob");
        customer2.setLastName("Smith");
        customer2.setEmail("bob.smith@example.com");
        customer2.setDateOfBirth(LocalDate.of(2000, 8, 22));
        customer2.setPhoneNumber("0987654321");
        customer2.setCity("Los Angeles");
        customer2.setState("California");
        customer2.setCountry("United States");
        customer2.setPincode("90210");

        Customer customer3 = new Customer();
        customer3.setFirstName("Carol");
        customer3.setLastName("Davis");
        customer3.setEmail("carol.davis@example.com");
        customer3.setDateOfBirth(LocalDate.of(1998, 12, 3));
        customer3.setPhoneNumber("5555555555");
        customer3.setCity("Chicago");
        customer3.setState("Illinois");
        customer3.setCountry("United States");
        customer3.setPincode("60601");

        Customer customer4 = new Customer();
        customer4.setFirstName("David");
        customer4.setLastName("Wilson");
        customer4.setEmail("david.wilson@example.com");
        customer4.setDateOfBirth(LocalDate.of(2001, 3, 10));
        customer4.setPhoneNumber("7777777777");
        customer4.setCity("Miami");
        customer4.setState("Florida");
        customer4.setCountry("United States");
        customer4.setPincode("33101");

        // Save sample data
        customerRepository.save(customer1);
        customerRepository.save(customer2);
        customerRepository.save(customer3);
        customerRepository.save(customer4);

        logger.info("Sample customer data initialized successfully!");
    }
}
