package com.example.ordermanagement.config;

import com.example.ordermanagement.model.*;
import com.example.ordermanagement.repository.*;
import com.example.ordermanagement.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private ShopRepository shopRepository;
    
    @Autowired
    private MenuItemRepository menuItemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private PermissionService permissionService;

    @Override
    public void run(String... args) throws Exception {
        // Initialize with sample data if database is empty
        if (customerRepository.count() == 0 && userRepository.count() == 0) {
            initializeSampleData();
            // Initialize permissions after sample data
            initializePermissions();
        }
    }

    private void initializeSampleData() {
        logger.info("Initializing sample data...");
        
        // Create sample users first
        initializeUsers();
        
        // Create sample customers
        Customer customer1 = new Customer();
        customer1.setFirstName("Alice");
        customer1.setLastName("Johnson");
        customer1.setEmail("alice.johnson@example.com");
        customer1.setDateOfBirth(LocalDate.of(1999, 5, 15));
        customer1.setPhoneNumber("9876543210");
        customer1.setCity("Mumbai");
        customer1.setState("Maharashtra");
        customer1.setCountry("India");
        customer1.setPincode("10001");

        Customer customer2 = new Customer();
        customer2.setFirstName("Bob");
        customer2.setLastName("Smith");
        customer2.setEmail("bob.smith@example.com");
        customer2.setDateOfBirth(LocalDate.of(2000, 8, 22));
        customer2.setPhoneNumber("8765432109");
        customer2.setCity("Delhi");
        customer2.setState("Delhi");
        customer2.setCountry("India");
        customer2.setPincode("90210");

        Customer customer3 = new Customer();
        customer3.setFirstName("Carol");
        customer3.setLastName("Davis");
        customer3.setEmail("carol.davis@example.com");
        customer3.setDateOfBirth(LocalDate.of(1998, 12, 3));
        customer3.setPhoneNumber("7654321098");
        customer3.setCity("Bangalore");
        customer3.setState("Karnataka");
        customer3.setCountry("India");
        customer3.setPincode("60601");

        Customer customer4 = new Customer();
        customer4.setFirstName("David");
        customer4.setLastName("Wilson");
        customer4.setEmail("david.wilson@example.com");
        customer4.setDateOfBirth(LocalDate.of(2001, 3, 10));
        customer4.setPhoneNumber("6543210987");
        customer4.setCity("Chennai");
        customer4.setState("Tamil Nadu");
        customer4.setCountry("India");
        customer4.setPincode("33101");

        // Save sample data
        customerRepository.save(customer1);
        customerRepository.save(customer2);
        customerRepository.save(customer3);
        customerRepository.save(customer4);

        // Create sample shops
        initializeShops();
        
        // Create sample menu items
        initializeMenuItems();

        logger.info("Sample data initialized successfully!");
    }
    
    private void initializeUsers() {
        // Create admin user only if it doesn't exist (check both email and username)
        if (!userRepository.existsByEmail("admin@ordermanagement.com") && 
            !userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@ordermanagement.com");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(Role.ADMIN);
            admin.setIsActive(true);
            userRepository.save(admin);
        }
        
        // Create shop owner user only if it doesn't exist (check both email and username)
        if (!userRepository.existsByEmail("shop@ordermanagement.com") && 
            !userRepository.existsByUsername("shopowner")) {
            User shopOwner = new User();
            shopOwner.setUsername("shopowner");
            shopOwner.setPassword(passwordEncoder.encode("shop123"));
            shopOwner.setEmail("shop@ordermanagement.com");
            shopOwner.setFirstName("Shop");
            shopOwner.setLastName("Owner");
            shopOwner.setRole(Role.SHOP);
            shopOwner.setIsActive(true);
            userRepository.save(shopOwner);
        }
        
        // Create customer user only if it doesn't exist (check both email and username)
        if (!userRepository.existsByEmail("customer@ordermanagement.com") && 
            !userRepository.existsByUsername("customer")) {
            User customer = new User();
            customer.setUsername("customer");
            customer.setPassword(passwordEncoder.encode("customer123"));
            customer.setEmail("customer@ordermanagement.com");
            customer.setFirstName("Customer");
            customer.setLastName("User");
            customer.setRole(Role.CUSTOMER);
            customer.setIsActive(true);
            userRepository.save(customer);
        }
        
        logger.info("Sample users initialized: admin/admin123, shopowner/shop123, customer/customer123");
    }
    
    private void initializeShops() {
        // Create sample shop 1 only if it doesn't exist
        if (!shopRepository.existsByEmail("mario@italiankit.com")) {
            // Create shop owner user for Mario's Italian Kitchen
            User marioUser = createShopOwnerUser("marios_mario", "mario@italiankit.com", "Mario", "Rossi");
            
            Shop shop1 = new Shop();
            shop1.setShopName("Mario's Italian Kitchen");
            shop1.setOwnerName("Mario Rossi");
            shop1.setEmail("mario@italiankit.com");
            shop1.setPhoneNumber("9123456789");
            shop1.setAddress("123 MG Road");
            shop1.setCity("Mumbai");
            shop1.setState("Maharashtra");
            shop1.setCountry("India");
            shop1.setPincode("10001");
            shop1.setShopType(ShopType.RESTAURANT);
            shop1.setStatus(ShopStatus.ACTIVE);
            shop1.setDescription("Authentic Italian cuisine with fresh ingredients");
            shop1.setOwnerUser(marioUser);
            Shop savedShop1 = shopRepository.save(shop1);
            
            // Update user's owned shop reference
            marioUser.setOwnedShop(savedShop1);
            userRepository.save(marioUser);
        }
        
        // Create sample shop 2 only if it doesn't exist
        if (!shopRepository.existsByEmail("takeshi@sakurasushi.com")) {
            // Create shop owner user for Sakura Sushi Bar
            User takeshiUser = createShopOwnerUser("sakura_takeshi", "takeshi@sakurasushi.com", "Takeshi", "Yamamoto");
            
            Shop shop2 = new Shop();
            shop2.setShopName("Sakura Sushi Bar");
            shop2.setOwnerName("Takeshi Yamamoto");
            shop2.setEmail("takeshi@sakurasushi.com");
            shop2.setPhoneNumber("8234567890");
            shop2.setAddress("456 Brigade Road");
            shop2.setCity("Bangalore");
            shop2.setState("Karnataka");
            shop2.setCountry("India");
            shop2.setPincode("90210");
            shop2.setShopType(ShopType.RESTAURANT);
            shop2.setStatus(ShopStatus.ACTIVE);
            shop2.setDescription("Fresh sushi and Japanese delicacies");
            shop2.setOwnerUser(takeshiUser);
            Shop savedShop2 = shopRepository.save(shop2);
            
            // Update user's owned shop reference
            takeshiUser.setOwnedShop(savedShop2);
            userRepository.save(takeshiUser);
        }
        
        // Create sample shop 3 only if it doesn't exist
        if (!shopRepository.existsByEmail("emma@greengarden.com")) {
            // Create shop owner user for Green Garden Cafe
            User emmaUser = createShopOwnerUser("greengarden_emma", "emma@greengarden.com", "Emma", "Thompson");
            
            Shop shop3 = new Shop();
            shop3.setShopName("Green Garden Cafe");
            shop3.setOwnerName("Emma Thompson");
            shop3.setEmail("emma@greengarden.com");
            shop3.setPhoneNumber("7345678901");
            shop3.setAddress("789 Connaught Place");
            shop3.setCity("Delhi");
            shop3.setState("Delhi");
            shop3.setCountry("India");
            shop3.setPincode("60601");
            shop3.setShopType(ShopType.CAFE);
            shop3.setStatus(ShopStatus.ACTIVE);
            shop3.setDescription("Healthy vegetarian and vegan options");
            shop3.setOwnerUser(emmaUser);
            Shop savedShop3 = shopRepository.save(shop3);
            
            // Update user's owned shop reference
            emmaUser.setOwnedShop(savedShop3);
            userRepository.save(emmaUser);
        }
        
        // Create a PENDING shop for testing activation
        if (!shopRepository.existsByEmail("test@pendingshop.com")) {
            // Create shop owner user for Test Pending Shop
            User testUser = createShopOwnerUser("testshop_test", "test@pendingshop.com", "Test", "Owner");
            
            Shop pendingShop = new Shop();
            pendingShop.setShopName("Test Pending Shop");
            pendingShop.setOwnerName("Test Owner");
            pendingShop.setEmail("test@pendingshop.com");
            pendingShop.setPhoneNumber("9999999999");
            pendingShop.setAddress("123 Test Street");
            pendingShop.setCity("Test City");
            pendingShop.setState("Test State");
            pendingShop.setCountry("India");
            pendingShop.setPincode("123456");
            pendingShop.setShopType(ShopType.RESTAURANT);
            pendingShop.setStatus(ShopStatus.PENDING);
            pendingShop.setDescription("A test shop in pending status for activation testing");
            pendingShop.setOwnerUser(testUser);
            Shop savedPendingShop = shopRepository.save(pendingShop);
            
            // Update user's owned shop reference
            testUser.setOwnedShop(savedPendingShop);
            userRepository.save(testUser);
        }
        
        logger.info("Sample shops created successfully!");
    }
    
    private void initializeMenuItems() {
        Shop shop1 = shopRepository.findAll().get(0); // Mario's Italian Kitchen
        Shop shop2 = shopRepository.findAll().get(1); // Sakura Sushi Bar
        Shop shop3 = shopRepository.findAll().get(2); // Green Garden Cafe
        
        // Mario's Italian Kitchen Menu Items
        createMenuItem(shop1, "Margherita Pizza", "Classic pizza with tomato sauce, mozzarella, and fresh basil", 
                      new BigDecimal("14.99"), MenuCategory.MAIN_COURSE, true, true, false, false, false, 20);
        createMenuItem(shop1, "Spaghetti Carbonara", "Traditional pasta with eggs, cheese, pancetta, and black pepper", 
                      new BigDecimal("16.99"), MenuCategory.MAIN_COURSE, true, false, false, false, false, 15);
        createMenuItem(shop1, "Caesar Salad", "Fresh romaine lettuce with Caesar dressing, croutons, and parmesan", 
                      new BigDecimal("9.99"), MenuCategory.APPETIZER, true, true, false, false, false, 10);
        createMenuItem(shop1, "Tiramisu", "Classic Italian dessert with coffee-soaked ladyfingers and mascarpone", 
                      new BigDecimal("7.99"), MenuCategory.DESSERT, true, true, false, false, false, 5);
        createMenuItem(shop1, "Italian Soda", "Refreshing sparkling water with fruit syrup", 
                      new BigDecimal("3.99"), MenuCategory.BEVERAGE, true, true, true, false, false, 2);
        
        // Sakura Sushi Bar Menu Items
        createMenuItem(shop2, "Salmon Nigiri", "Fresh salmon over seasoned sushi rice", 
                      new BigDecimal("12.99"), MenuCategory.MAIN_COURSE, true, false, false, false, false, 10);
        createMenuItem(shop2, "California Roll", "Crab, avocado, and cucumber roll with sesame seeds", 
                      new BigDecimal("8.99"), MenuCategory.MAIN_COURSE, true, false, false, false, false, 8);
        createMenuItem(shop2, "Miso Soup", "Traditional Japanese soup with tofu and seaweed", 
                      new BigDecimal("4.99"), MenuCategory.APPETIZER, true, true, true, false, false, 5);
        createMenuItem(shop2, "Edamame", "Steamed young soybeans with sea salt", 
                      new BigDecimal("5.99"), MenuCategory.APPETIZER, true, true, true, false, false, 3);
        createMenuItem(shop2, "Green Tea", "Premium Japanese green tea", 
                      new BigDecimal("2.99"), MenuCategory.BEVERAGE, true, true, true, false, false, 2);
        
        // Green Garden Cafe Menu Items
        createMenuItem(shop3, "Quinoa Buddha Bowl", "Nutritious bowl with quinoa, roasted vegetables, and tahini dressing", 
                      new BigDecimal("13.99"), MenuCategory.MAIN_COURSE, true, true, true, true, false, 12);
        createMenuItem(shop3, "Avocado Toast", "Whole grain bread topped with smashed avocado and hemp seeds", 
                      new BigDecimal("8.99"), MenuCategory.APPETIZER, true, true, true, false, false, 5);
        createMenuItem(shop3, "Acai Bowl", "Acai berry smoothie bowl topped with granola and fresh fruits", 
                      new BigDecimal("11.99"), MenuCategory.DESSERT, true, true, true, false, false, 8);
        createMenuItem(shop3, "Spicy Lentil Soup", "Hearty soup with red lentils and warming spices", 
                      new BigDecimal("7.99"), MenuCategory.MAIN_COURSE, true, true, true, false, true, 10);
        createMenuItem(shop3, "Cold Brew Coffee", "Smooth cold-brewed coffee served over ice", 
                      new BigDecimal("4.99"), MenuCategory.BEVERAGE, true, true, true, false, false, 3);
        
        logger.info("Sample menu items created successfully!");
    }
    
    private void createMenuItem(Shop shop, String name, String description, BigDecimal price, 
                               MenuCategory category, boolean isAvailable, boolean isVegetarian, 
                               boolean isVegan, boolean isGlutenFree, boolean isSpicy, int prepTime) {
        MenuItem menuItem = new MenuItem();
        menuItem.setShop(shop);
        menuItem.setItemName(name);
        menuItem.setDescription(description);
        menuItem.setPrice(price);
        menuItem.setCategory(category);
        menuItem.setIsAvailable(isAvailable);
        menuItem.setIsVegetarian(isVegetarian);
        menuItem.setIsVegan(isVegan);
        // Note: isGlutenFree and isSpicy fields don't exist in current MenuItem model
        menuItem.setPreparationTimeMinutes(prepTime);
        menuItem.setImageUrl("/images/default-food.jpg"); // Default image for now
        menuItemRepository.save(menuItem);
    }

    private void initializePermissions() {
        logger.info("Initializing permission system...");
        
        try {
            // Create system permissions
            createSystemPermissions();
            
            // Assign default permissions to users
            assignDefaultPermissions();
            
            logger.info("Permission system initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing permission system", e);
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
            Optional<User> adminUserOpt = userRepository.findByUsername("admin");
            if (adminUserOpt.isPresent()) {
                assignAdminPermissions(adminUserOpt.get());
            }
        } catch (Exception e) {
            logger.warn("Error assigning admin permissions: {}", e.getMessage());
        }

        // Get shop user
        try {
            Optional<User> shopUserOpt = userRepository.findByUsername("shopowner");
            if (shopUserOpt.isEmpty()) {
                shopUserOpt = userRepository.findByUsername("shop");
            }
            if (shopUserOpt.isPresent()) {
                assignShopPermissions(shopUserOpt.get());
            }
        } catch (Exception e) {
            logger.warn("Error assigning shop permissions: {}", e.getMessage());
        }

        // Get customer user
        try {
            Optional<User> customerUserOpt = userRepository.findByUsername("customer");
            if (customerUserOpt.isPresent()) {
                assignCustomerPermissions(customerUserOpt.get());
            }
        } catch (Exception e) {
            logger.warn("Error assigning customer permissions: {}", e.getMessage());
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
    
    /**
     * Helper method to create shop owner user accounts
     */
    private User createShopOwnerUser(String username, String email, String firstName, String lastName) {
        // Check if user already exists
        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            // Return existing user if found
            Optional<User> existingUser = userRepository.findByUsername(username);
            if (existingUser.isPresent()) {
                return existingUser.get();
            }
            existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                return existingUser.get();
            }
        }
        
        // Create new shop owner user
        User shopOwner = new User();
        shopOwner.setUsername(username);
        shopOwner.setPassword(passwordEncoder.encode("shop123")); // Default password
        shopOwner.setEmail(email);
        shopOwner.setFirstName(firstName);
        shopOwner.setLastName(lastName);
        shopOwner.setRole(Role.SHOP);
        shopOwner.setIsActive(true);
        
        User savedUser = userRepository.save(shopOwner);
        logger.info("Created shop owner account: {} for {}", username, firstName + " " + lastName);
        
        return savedUser;
    }
}
