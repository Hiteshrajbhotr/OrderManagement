package com.example.ordermanagement.config;

import com.example.ordermanagement.model.Role;
import com.example.ordermanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// @Component - Disabled to prevent conflicts with DataInitializer
public class UserDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(UserDataInitializer.class);

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        // Clean up old accounts first
        cleanupOldAccounts();
        
        // Create default admin user if not exists
        if (!userService.existsByUsername("admin")) {
            userService.createUser(
                "admin",
                "admin@ordermanagement.com",
                "admin123",
                "System",
                "Administrator",
                Role.ADMIN
            );
            logger.info("Default admin user created: admin / admin123");
        }

        // Create default shop user if not exists
        if (!userService.existsByUsername("shop")) {
            userService.createUser(
                "shop",
                "shop@ordermanagement.com",
                "shop123",
                "John",
                "Smith",
                Role.SHOP
            );
            logger.info("Default shop user created: shop / shop123");
        }

        // Create default customer user if not exists
        if (!userService.existsByUsername("customer")) {
            userService.createUser(
                "customer",
                "customer@ordermanagement.com",
                "customer123",
                "Jane",
                "Doe",
                Role.CUSTOMER
            );
            logger.info("Default customer user created: customer / customer123");
        }

        // Create additional sample users
        if (!userService.existsByUsername("shop2")) {
            userService.createUser(
                "shop2",
                "mary.johnson@ordermanagement.com",
                "shop456",
                "Mary",
                "Johnson",
                Role.SHOP
            );
            logger.info("Sample shop user created: shop2 / shop456");
        }

        if (!userService.existsByUsername("customer2")) {
            userService.createUser(
                "customer2",
                "bob.wilson@ordermanagement.com",
                "customer456",
                "Bob",
                "Wilson",
                Role.CUSTOMER
            );
            logger.info("Sample customer user created: customer2 / customer456");
        }

        logger.info("User data initialization completed!");
        logger.info("Available test accounts:");
        logger.info("   Admin: admin / admin123");
        logger.info("   Shop: shop / shop123");
        logger.info("   Customer: customer / customer123");
    }

    private void cleanupOldAccounts() {
        // Delete old shop accounts (previously teacher accounts)
        try {
            if (userService.existsByUsername("teacher")) {
                userService.deleteUserByUsername("teacher");
                logger.info("Deleted old teacher account (now using shop accounts)");
            }
            if (userService.existsByUsername("teacher2")) {
                userService.deleteUserByUsername("teacher2");
                logger.info("Deleted old teacher2 account (now using shop accounts)");
            }
        } catch (Exception e) {
            logger.warn("Could not delete old teacher accounts: {}", e.getMessage());
        }

        // Delete old customer accounts (previously student accounts)
        try {
            if (userService.existsByUsername("student")) {
                userService.deleteUserByUsername("student");
                logger.info("Deleted old student account (now using customer accounts)");
            }
            if (userService.existsByUsername("student2")) {
                userService.deleteUserByUsername("student2");
                logger.info("Deleted old student2 account (now using customer accounts)");
            }
        } catch (Exception e) {
            logger.warn("Could not delete old student accounts: {}", e.getMessage());
        }
    }
}
