package com.example.ordermanagement.config;

import com.example.ordermanagement.model.*;
import com.example.ordermanagement.repository.ShopRepository;
import com.example.ordermanagement.repository.MenuItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

// @Component - Disabled to prevent conflicts with DataInitializer
// @Order(2) // Run after UserDataInitializer
public class ShopDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ShopDataInitializer.class);

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Only initialize if no shops exist
            long shopCount = shopRepository.count();
            logger.info("Current shop count in database: {}", shopCount);
            
            if (shopCount == 0) {
                initializeSampleShops();
                logger.info("Sample shops and menu items initialized!");
            } else {
                logger.info("Shops already exist, skipping sample data initialization");
                // Debug: Let's see what shops exist
                var existingShops = shopRepository.findAll();
                logger.info("Existing shops in database:");
                for (Shop shop : existingShops) {
                    logger.info("  - {} ({}) - Status: {}", shop.getShopName(), shop.getEmail(), shop.getStatus());
                }
            }
        } catch (Exception e) {
            logger.error("Error during shop data initialization: {}", e.getMessage());
            logger.info("Application will continue without sample data");
        }
    }

    private void initializeSampleShops() {
        // Create sample shops only if they don't already exist
        Shop pizzaPalace = null;
        if (!shopRepository.existsByEmail("mario@pizzapalace.com")) {
            pizzaPalace = createShop(
                "Pizza Palace",
                "Mario Rossi",
                "mario@pizzapalace.com",
                "15550101234",
                "123 Main Street, Suite 100",
                "New York",
                "NY",
                "USA",
                "10001",
                ShopType.RESTAURANT,
                "Authentic Italian pizza and pasta restaurant serving fresh, handmade dishes since 1985.",
                ShopStatus.ACTIVE
            );
        } else {
            pizzaPalace = shopRepository.findByEmail("mario@pizzapalace.com").orElse(null);
        }

        Shop coffeeCentral = null;
        if (!shopRepository.existsByEmail("sarah@coffeecentral.com")) {
            coffeeCentral = createShop(
                "Coffee Central",
                "Sarah Johnson",
                "sarah@coffeecentral.com",
                "15550202345",
                "456 Oak Avenue",
                "San Francisco",
                "CA",
                "USA",
                "94102",
                ShopType.CAFE,
                "Premium coffee shop specializing in artisan roasted beans and handcrafted beverages.",
                ShopStatus.ACTIVE
            );
        } else {
            coffeeCentral = shopRepository.findByEmail("sarah@coffeecentral.com").orElse(null);
        }

        Shop sweetTreats = null;
        if (!shopRepository.existsByEmail("emily@sweettreats.com")) {
            sweetTreats = createShop(
                "Sweet Treats Bakery",
                "Emily Chen",
                "emily@sweettreats.com",
                "15550303456",
                "789 Baker Street",
                "Chicago",
                "IL",
                "USA",
                "60601",
                ShopType.BAKERY,
                "Family-owned bakery offering fresh pastries, cakes, and artisan breads daily.",
                ShopStatus.ACTIVE
            );
        } else {
            sweetTreats = shopRepository.findByEmail("emily@sweettreats.com").orElse(null);
        }

        Shop quickBite = null;
        if (!shopRepository.existsByEmail("ahmed@quickbite.com")) {
            quickBite = createShop(
                "Quick Bite Express",
                "Ahmed Hassan",
                "ahmed@quickbite.com",
                "15550404567",
                "321 Fast Lane",
                "Austin",
                "TX",
                "USA",
                "73301",
                ShopType.FAST_FOOD,
                "Fast and fresh food with a focus on healthy, quick meal options.",
                ShopStatus.PENDING
            );
        } else {
            quickBite = shopRepository.findByEmail("ahmed@quickbite.com").orElse(null);
        }

        Shop techMart = null;
        if (!shopRepository.existsByEmail("david@techmart.com")) {
            techMart = createShop(
                "Tech Mart Electronics",
                "David Kim",
                "david@techmart.com",
                "15550505678",
                "654 Silicon Drive",
                "Seattle",
                "WA",
                "USA",
                "98101",
                ShopType.ELECTRONICS,
                "Your one-stop shop for the latest electronics, gadgets, and tech accessories.",
                ShopStatus.ACTIVE
            );
        } else {
            techMart = shopRepository.findByEmail("david@techmart.com").orElse(null);
        }

        // Create menu items for Pizza Palace (only if shop exists and doesn't have menu items)
        if (pizzaPalace != null && menuItemRepository.findByShopId(pizzaPalace.getId()).isEmpty()) {
            createMenuItem(pizzaPalace, "Margherita Pizza", "Classic pizza with fresh mozzarella, tomatoes, and basil", 
                          new BigDecimal("14.99"), MenuCategory.MAIN_COURSE, true, true, false, false, null);
            createMenuItem(pizzaPalace, "Pepperoni Pizza", "Traditional pepperoni pizza with mozzarella cheese", 
                          new BigDecimal("16.99"), MenuCategory.MAIN_COURSE, true, false, false, false, null);
            createMenuItem(pizzaPalace, "Caesar Salad", "Fresh romaine lettuce with parmesan cheese and croutons", 
                          new BigDecimal("9.99"), MenuCategory.SALAD, true, true, false, false, null);
            createMenuItem(pizzaPalace, "Tiramisu", "Classic Italian dessert with mascarpone and coffee", 
                          new BigDecimal("7.99"), MenuCategory.DESSERT, true, true, false, false, null);
            createMenuItem(pizzaPalace, "Italian Soda", "Refreshing sparkling water with natural fruit flavors", 
                          new BigDecimal("3.99"), MenuCategory.BEVERAGE, true, true, true, false, null);
        }

        // Create menu items for Coffee Central (only if shop exists and doesn't have menu items)
        if (coffeeCentral != null && menuItemRepository.findByShopId(coffeeCentral.getId()).isEmpty()) {
            createMenuItem(coffeeCentral, "Espresso", "Rich and bold single shot of premium espresso", 
                          new BigDecimal("2.99"), MenuCategory.COFFEE, true, true, true, false, null);
            createMenuItem(coffeeCentral, "Cappuccino", "Perfect balance of espresso, steamed milk, and foam", 
                          new BigDecimal("4.99"), MenuCategory.COFFEE, true, true, false, false, null);
            createMenuItem(coffeeCentral, "Avocado Toast", "Fresh avocado on artisan sourdough with sea salt", 
                          new BigDecimal("8.99"), MenuCategory.BREAKFAST, true, true, true, false, null);
            createMenuItem(coffeeCentral, "Blueberry Muffin", "Freshly baked muffin with organic blueberries", 
                          new BigDecimal("3.99"), MenuCategory.BREAKFAST, true, true, false, false, null);
            createMenuItem(coffeeCentral, "Green Smoothie", "Spinach, banana, apple, and coconut water blend", 
                          new BigDecimal("6.99"), MenuCategory.BEVERAGE, true, true, true, false, null);
        }

        // Create menu items for Sweet Treats Bakery (only if shop exists and doesn't have menu items)
        if (sweetTreats != null && menuItemRepository.findByShopId(sweetTreats.getId()).isEmpty()) {
            createMenuItem(sweetTreats, "Chocolate Croissant", "Buttery croissant filled with premium dark chocolate", 
                          new BigDecimal("4.99"), MenuCategory.BAKERY, true, true, false, false, null);
            createMenuItem(sweetTreats, "Red Velvet Cupcake", "Moist red velvet cake with cream cheese frosting", 
                          new BigDecimal("3.99"), MenuCategory.DESSERT, true, true, false, false, null);
            createMenuItem(sweetTreats, "Artisan Sourdough", "Traditional sourdough bread baked fresh daily", 
                          new BigDecimal("5.99"), MenuCategory.BAKERY, true, true, true, false, null);
            createMenuItem(sweetTreats, "Fruit Tart", "Seasonal fresh fruit on vanilla custard base", 
                          new BigDecimal("6.99"), MenuCategory.DESSERT, true, true, false, false, null);
            createMenuItem(sweetTreats, "Herbal Tea", "Selection of organic herbal teas", 
                          new BigDecimal("2.99"), MenuCategory.TEA, true, true, true, false, null);
        }

        // Create menu items for Quick Bite Express (only if shop exists and doesn't have menu items)
        if (quickBite != null && menuItemRepository.findByShopId(quickBite.getId()).isEmpty()) {
            createMenuItem(quickBite, "Grilled Chicken Wrap", "Healthy grilled chicken with fresh vegetables", 
                          new BigDecimal("8.99"), MenuCategory.MAIN_COURSE, true, false, false, false, null);
            createMenuItem(quickBite, "Veggie Burger", "Plant-based patty with fresh toppings", 
                          new BigDecimal("9.99"), MenuCategory.BURGER, true, true, true, false, null);
            createMenuItem(quickBite, "Sweet Potato Fries", "Crispy baked sweet potato fries with herbs", 
                          new BigDecimal("4.99"), MenuCategory.SNACK, true, true, true, false, null);
            createMenuItem(quickBite, "Fresh Fruit Bowl", "Seasonal mixed fruits with honey drizzle", 
                          new BigDecimal("5.99"), MenuCategory.SNACK, true, true, true, false, null);
        }

        logger.info("Created {} sample shops", shopRepository.count());
        logger.info("Created {} sample menu items", menuItemRepository.count());
    }

    private Shop createShop(String shopName, String ownerName, String email, String phoneNumber,
                           String address, String city, String state, String country, String pincode,
                           ShopType shopType, String description, ShopStatus status) {
        Shop shop = new Shop();
        shop.setShopName(shopName);
        shop.setOwnerName(ownerName);
        shop.setEmail(email);
        shop.setPhoneNumber(phoneNumber);
        shop.setAddress(address);
        shop.setCity(city);
        shop.setState(state);
        shop.setCountry(country);
        shop.setPincode(pincode);
        shop.setShopType(shopType);
        shop.setDescription(description);
        shop.setStatus(status);
        // Note: registrationDate is set automatically by @PrePersist in Shop entity
        
        return shopRepository.save(shop);
    }

    private MenuItem createMenuItem(Shop shop, String itemName, String description, BigDecimal price,
                                  MenuCategory category, boolean isAvailable, boolean isVegetarian,
                                  boolean isVegan, boolean isSpicy, String imageUrl) {
        MenuItem menuItem = new MenuItem();
        menuItem.setShop(shop);
        menuItem.setItemName(itemName);
        menuItem.setDescription(description);
        menuItem.setPrice(price);
        menuItem.setCategory(category);
        menuItem.setIsAvailable(isAvailable);
        menuItem.setIsVegetarian(isVegetarian);
        menuItem.setIsVegan(isVegan);
        menuItem.setImageUrl(imageUrl);
        
        return menuItemRepository.save(menuItem);
    }
}
