package com.example.ordermanagement.repository;

import com.example.ordermanagement.model.Shop;
import com.example.ordermanagement.model.ShopStatus;
import com.example.ordermanagement.model.ShopType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    
    // Find shops by email
    Optional<Shop> findByEmail(String email);
    
    // Find shops by owner name
    List<Shop> findByOwnerNameContainingIgnoreCase(String ownerName);
    
    // Find shops by shop name
    List<Shop> findByShopNameContainingIgnoreCase(String shopName);
    
    // Find shops by status
    List<Shop> findByStatus(ShopStatus status);
    
    // Find shops by status with pagination
    Page<Shop> findByStatus(ShopStatus status, Pageable pageable);
    
    // Find shops by shop type
    List<Shop> findByShopType(ShopType shopType);
    
    // Find shops by shop type with pagination
    Page<Shop> findByShopType(ShopType shopType, Pageable pageable);
    
    // Find shops by city
    List<Shop> findByCity(String city);
    
    // Find shops by city with pagination
    Page<Shop> findByCity(String city, Pageable pageable);
    
    // Find shops by state
    List<Shop> findByState(String state);
    
    // Find shops by country
    List<Shop> findByCountry(String country);
    
    // Find active shops
    @Query("SELECT s FROM Shop s WHERE s.status = 'ACTIVE'")
    List<Shop> findActiveShops();
    
    // Find pending shops (for admin approval)
    @Query("SELECT s FROM Shop s WHERE s.status = 'PENDING' ORDER BY s.registrationDate ASC")
    List<Shop> findPendingShops();
    
    // Find shops registered in the last N days
    @Query("SELECT s FROM Shop s WHERE s.registrationDate >= :fromDate ORDER BY s.registrationDate DESC")
    List<Shop> findShopsRegisteredAfter(@Param("fromDate") LocalDateTime fromDate);
    
    // Find shops by location (city and state)
    @Query("SELECT s FROM Shop s WHERE s.city = :city AND s.state = :state")
    List<Shop> findByLocation(@Param("city") String city, @Param("state") String state);
    
    // Search shops by multiple criteria
    @Query("SELECT s FROM Shop s WHERE " +
           "(:shopName IS NULL OR LOWER(s.shopName) LIKE LOWER(CONCAT('%', :shopName, '%'))) AND " +
           "(:ownerName IS NULL OR LOWER(s.ownerName) LIKE LOWER(CONCAT('%', :ownerName, '%'))) AND " +
           "(:city IS NULL OR LOWER(s.city) = LOWER(:city)) AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:shopType IS NULL OR s.shopType = :shopType)")
    List<Shop> searchShops(@Param("shopName") String shopName,
                          @Param("ownerName") String ownerName,
                          @Param("city") String city,
                          @Param("status") ShopStatus status,
                          @Param("shopType") ShopType shopType);
    
    // Count shops by status
    @Query("SELECT COUNT(s) FROM Shop s WHERE s.status = :status")
    long countByStatus(@Param("status") ShopStatus status);
    
    // Count shops by shop type
    @Query("SELECT COUNT(s) FROM Shop s WHERE s.shopType = :shopType")
    long countByShopType(@Param("shopType") ShopType shopType);
    
    // Find shops with menu items count
    @Query("SELECT s FROM Shop s LEFT JOIN s.menuItems m GROUP BY s HAVING COUNT(m) > 0")
    List<Shop> findShopsWithMenuItems();
    
    // Find shops without menu items
    @Query("SELECT s FROM Shop s LEFT JOIN s.menuItems m GROUP BY s HAVING COUNT(m) = 0")
    List<Shop> findShopsWithoutMenuItems();
    
    // Check if email exists (for validation)
    boolean existsByEmail(String email);
    
    // Check if shop name exists in same city (for validation)
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Shop s WHERE s.shopName = :shopName AND s.city = :city")
    boolean existsByShopNameAndCity(@Param("shopName") String shopName, @Param("city") String city);
    
    // Search method with pageable support
    Page<Shop> findByShopNameContainingIgnoreCaseOrOwnerNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String shopName, String ownerName, String email, Pageable pageable);
}
