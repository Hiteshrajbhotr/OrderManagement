package com.example.ordermanagement.repository;

import com.example.ordermanagement.model.MenuItem;
import com.example.ordermanagement.model.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    
    // Find menu items by shop ID
    List<MenuItem> findByShopId(Long shopId);
    
    // Find available menu items by shop ID
    @Query("SELECT m FROM MenuItem m WHERE m.shop.id = :shopId AND m.isAvailable = true")
    List<MenuItem> findAvailableItemsByShopId(@Param("shopId") Long shopId);
    
    // Find all available menu items (for customer dashboard)
    List<MenuItem> findByIsAvailableTrue();
    
    // Find menu items by category
    List<MenuItem> findByCategory(MenuCategory category);
    
    // Find menu items by shop ID and category
    @Query("SELECT m FROM MenuItem m WHERE m.shop.id = :shopId AND m.category = :category")
    List<MenuItem> findByShopIdAndCategory(@Param("shopId") Long shopId, @Param("category") MenuCategory category);
    
    // Find menu items by name (search)
    List<MenuItem> findByItemNameContainingIgnoreCase(String itemName);
    
    // Find menu items by shop and name search
    @Query("SELECT m FROM MenuItem m WHERE m.shop.id = :shopId AND LOWER(m.itemName) LIKE LOWER(CONCAT('%', :itemName, '%'))")
    List<MenuItem> findByShopIdAndItemNameContaining(@Param("shopId") Long shopId, @Param("itemName") String itemName);
    
    // Find vegetarian items by shop
    @Query("SELECT m FROM MenuItem m WHERE m.shop.id = :shopId AND m.isVegetarian = true")
    List<MenuItem> findVegetarianItemsByShopId(@Param("shopId") Long shopId);
    
    // Find vegan items by shop
    @Query("SELECT m FROM MenuItem m WHERE m.shop.id = :shopId AND m.isVegan = true")
    List<MenuItem> findVeganItemsByShopId(@Param("shopId") Long shopId);
    
    // Find items by price range
    @Query("SELECT m FROM MenuItem m WHERE m.price BETWEEN :minPrice AND :maxPrice")
    List<MenuItem> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    // Find items by shop and price range
    @Query("SELECT m FROM MenuItem m WHERE m.shop.id = :shopId AND m.price BETWEEN :minPrice AND :maxPrice")
    List<MenuItem> findByShopIdAndPriceRange(@Param("shopId") Long shopId, 
                                           @Param("minPrice") BigDecimal minPrice, 
                                           @Param("maxPrice") BigDecimal maxPrice);
    
    // Count menu items by shop
    @Query("SELECT COUNT(m) FROM MenuItem m WHERE m.shop.id = :shopId")
    long countByShopId(@Param("shopId") Long shopId);
    
    // Count available menu items by shop
    @Query("SELECT COUNT(m) FROM MenuItem m WHERE m.shop.id = :shopId AND m.isAvailable = true")
    long countAvailableByShopId(@Param("shopId") Long shopId);
    
    // Find items with preparation time less than specified minutes
    @Query("SELECT m FROM MenuItem m WHERE m.shop.id = :shopId AND m.preparationTimeMinutes <= :maxTime")
    List<MenuItem> findQuickItemsByShopId(@Param("shopId") Long shopId, @Param("maxTime") Integer maxTime);
    
    // Search menu items by multiple criteria
    @Query("SELECT m FROM MenuItem m WHERE " +
           "(:shopId IS NULL OR m.shop.id = :shopId) AND " +
           "(:itemName IS NULL OR LOWER(m.itemName) LIKE LOWER(CONCAT('%', :itemName, '%'))) AND " +
           "(:category IS NULL OR m.category = :category) AND " +
           "(:isAvailable IS NULL OR m.isAvailable = :isAvailable) AND " +
           "(:isVegetarian IS NULL OR m.isVegetarian = :isVegetarian) AND " +
           "(:isVegan IS NULL OR m.isVegan = :isVegan) AND " +
           "(:minPrice IS NULL OR m.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR m.price <= :maxPrice)")
    List<MenuItem> searchMenuItems(@Param("shopId") Long shopId,
                                 @Param("itemName") String itemName,
                                 @Param("category") MenuCategory category,
                                 @Param("isAvailable") Boolean isAvailable,
                                 @Param("isVegetarian") Boolean isVegetarian,
                                 @Param("isVegan") Boolean isVegan,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice);
    
    // Find most expensive items by shop
    @Query("SELECT m FROM MenuItem m WHERE m.shop.id = :shopId ORDER BY m.price DESC")
    List<MenuItem> findItemsByShopIdOrderByPriceDesc(@Param("shopId") Long shopId);
    
    // Find cheapest items by shop
    @Query("SELECT m FROM MenuItem m WHERE m.shop.id = :shopId ORDER BY m.price ASC")
    List<MenuItem> findItemsByShopIdOrderByPriceAsc(@Param("shopId") Long shopId);
    
    // Find newest items by shop
    @Query("SELECT m FROM MenuItem m WHERE m.shop.id = :shopId ORDER BY m.createdAt DESC")
    List<MenuItem> findItemsByShopIdOrderByCreatedAtDesc(@Param("shopId") Long shopId);
    
    // Get average price of items in a shop
    @Query("SELECT AVG(m.price) FROM MenuItem m WHERE m.shop.id = :shopId")
    BigDecimal getAveragePriceByShopId(@Param("shopId") Long shopId);
    
    // Check if item name exists in shop (for validation)
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM MenuItem m WHERE m.shop.id = :shopId AND m.itemName = :itemName")
    boolean existsByShopIdAndItemName(@Param("shopId") Long shopId, @Param("itemName") String itemName);
    
    // Delete all items by shop ID (for shop deletion)
    void deleteByShopId(Long shopId);
}
