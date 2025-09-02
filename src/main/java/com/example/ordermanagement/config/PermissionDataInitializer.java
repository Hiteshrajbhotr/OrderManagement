package com.example.ordermanagement.config;

import com.example.ordermanagement.model.Permission;
import com.example.ordermanagement.model.Role;
import com.example.ordermanagement.model.User;
import com.example.ordermanagement.service.PermissionService;
import com.example.ordermanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Order(3) // Run after user data initialization and other components
public class PermissionDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PermissionDataInitializer.class);

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting permission data initialization...");
        
        try {
            // Add a small delay to ensure all services are fully initialized
            Thread.sleep(1000);
            
            createSystemPermissions();
            assignDefaultPermissions();
            logger.info("Permission data initialization completed successfully");
        } catch (Exception e) {
            logger.error("Error during permission data initialization", e);
            throw e; // Re-throw to ensure startup fails if permissions can't be initialized
        }
    }

    private void createSystemPermissions() {
        logger.info("Creating system permissions...");

        // User Management Permissions
        createPermissionIfNotExists("Manage Users", "Full user management access", "users", "manage");
        createPermissionIfNotExists("View Users", "View user information", "users", "view");
        createPermissionIfNotExists("Create Users", "Create new users", "users", "create");
        createPermissionIfNotExists("Edit Users", "Edit user information", "users", "edit");
        createPermissionIfNotExists("Delete Users", "Delete users", "users", "delete");

        // Shop Management Permissions
        createPermissionIfNotExists("Manage Shops", "Full shop management access", "shops", "manage");
        createPermissionIfNotExists("View Shops", "View shop information", "shops", "view");
        createPermissionIfNotExists("Create Shops", "Create new shops", "shops", "create");
        createPermissionIfNotExists("Edit Shops", "Edit shop information", "shops", "edit");
        createPermissionIfNotExists("Delete Shops", "Delete shops", "shops", "delete");

        // Menu Item Permissions
        createPermissionIfNotExists("Manage Menu Items", "Full menu item management", "menu-items", "manage");
        createPermissionIfNotExists("View Menu Items", "View menu items", "menu-items", "view");
        createPermissionIfNotExists("Create Menu Items", "Create new menu items", "menu-items", "create");
        createPermissionIfNotExists("Edit Menu Items", "Edit menu items", "menu-items", "edit");
        createPermissionIfNotExists("Delete Menu Items", "Delete menu items", "menu-items", "delete");

        // Order Management Permissions
        createPermissionIfNotExists("Manage Orders", "Full order management access", "orders", "manage");
        createPermissionIfNotExists("View Orders", "View order information", "orders", "view");
        createPermissionIfNotExists("Create Orders", "Create new orders", "orders", "create");
        createPermissionIfNotExists("Edit Orders", "Edit order information", "orders", "edit");
        createPermissionIfNotExists("Cancel Orders", "Cancel orders", "orders", "cancel");

        // Dashboard Permissions
        createPermissionIfNotExists("Admin Dashboard", "Access admin dashboard", "dashboard", "admin");
        createPermissionIfNotExists("Shop Dashboard", "Access shop dashboard", "dashboard", "shop");
        createPermissionIfNotExists("Customer Dashboard", "Access customer dashboard", "dashboard", "customer");

        // Permission Management
        createPermissionIfNotExists("Manage Permissions", "Full permission management access", "permissions", "manage");
        createPermissionIfNotExists("View Permissions", "View permission information", "permissions", "view");

        // Cart Management
        createPermissionIfNotExists("Manage Cart", "Full cart management access", "cart", "manage");
        createPermissionIfNotExists("View Cart", "View cart contents", "cart", "view");

        // Reporting Permissions
        createPermissionIfNotExists("View Reports", "Access reporting features", "reports", "view");
        createPermissionIfNotExists("Export Data", "Export system data", "data", "export");

        logger.info("System permissions created successfully");
    }

    private void createPermissionIfNotExists(String name, String description, String resource, String action) {
        try {
            Optional<Permission> existingPermission = permissionService.getPermissionByName(name);
            if (existingPermission.isEmpty()) {
                Permission permission = permissionService.createPermission(name, description, resource, action);
                logger.debug("Created permission: {}", permission.getName());
            } else {
                logger.debug("Permission already exists: {}", name);
            }
        } catch (Exception e) {
            logger.error("Error creating permission: {}", name, e);
        }
    }

    private void assignDefaultPermissions() {
        logger.info("Assigning default permissions to users...");

        // Get admin user
        try {
            User adminUser = userService.findByUsername("admin");
            assignAdminPermissions(adminUser);
        } catch (RuntimeException e) {
            logger.warn("Admin user not found: {}", e.getMessage());
        }

        // Get shop user
        try {
            User shopUser = userService.findByUsername("shopowner");
            assignShopPermissions(shopUser);
        } catch (RuntimeException e) {
            try {
                User shopUser = userService.findByUsername("shop");
                assignShopPermissions(shopUser);
            } catch (RuntimeException ex) {
                logger.warn("Shop user not found: {}", ex.getMessage());
            }
        }

        // Get customer user
        try {
            User customerUser = userService.findByUsername("customer");
            assignCustomerPermissions(customerUser);
        } catch (RuntimeException e) {
            logger.warn("Customer user not found: {}", e.getMessage());
        }

        logger.info("Default permissions assigned successfully");
    }

    private void assignAdminPermissions(User adminUser) {
        logger.info("Assigning admin permissions to user: {}", adminUser.getUsername());

        // Admin gets all permissions
        List<String> adminPermissions = List.of(
            "Manage Users", "View Users", "Create Users", "Edit Users", "Delete Users",
            "Manage Shops", "View Shops", "Create Shops", "Edit Shops", "Delete Shops",
            "Manage Menu Items", "View Menu Items", "Create Menu Items", "Edit Menu Items", "Delete Menu Items",
            "Manage Orders", "View Orders", "Create Orders", "Edit Orders", "Cancel Orders",
            "Admin Dashboard", "Shop Dashboard", "Customer Dashboard",
            "Manage Permissions", "View Permissions",
            "Manage Cart", "View Cart",
            "View Reports", "Export Data"
        );

        assignPermissionsToUser(adminUser, adminPermissions);
    }

    private void assignShopPermissions(User shopUser) {
        logger.info("Assigning shop permissions to user: {}", shopUser.getUsername());

        // Shop owners get limited permissions
        List<String> shopPermissions = List.of(
            "View Shops", "Edit Shops", // Can view and edit their own shop
            "Manage Menu Items", "View Menu Items", "Create Menu Items", "Edit Menu Items", "Delete Menu Items",
            "View Orders", "Edit Orders", // Can manage orders for their shop
            "Shop Dashboard",
            "View Cart" // Can view customer carts
        );

        assignPermissionsToUser(shopUser, shopPermissions);
    }

    private void assignCustomerPermissions(User customerUser) {
        logger.info("Assigning customer permissions to user: {}", customerUser.getUsername());

        // Customers get basic permissions
        List<String> customerPermissions = List.of(
            "View Shops", "View Menu Items",
            "Create Orders", "View Orders",
            "Customer Dashboard",
            "Manage Cart", "View Cart"
        );

        assignPermissionsToUser(customerUser, customerPermissions);
    }

    private void assignPermissionsToUser(User user, List<String> permissionNames) {
        for (String permissionName : permissionNames) {
            try {
                Optional<Permission> permission = permissionService.getPermissionByName(permissionName);
                if (permission.isPresent()) {
                    // Check if user already has this permission
                    if (!permissionService.hasPermissionByName(user.getId(), permissionName)) {
                        permissionService.grantPermissionToUser(
                            user.getId(), 
                            permission.get().getId(), 
                            user.getId() // Self-granted during initialization
                        );
                        logger.debug("Granted permission '{}' to user '{}'", permissionName, user.getUsername());
                    }
                } else {
                    logger.warn("Permission not found: {}", permissionName);
                }
            } catch (Exception e) {
                logger.error("Error assigning permission '{}' to user '{}'", permissionName, user.getUsername(), e);
            }
        }
    }
}
