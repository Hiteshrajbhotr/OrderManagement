package com.example.ordermanagement.service;

import com.example.ordermanagement.dto.ShopRequest;
import com.example.ordermanagement.dto.ShopResponse;
import com.example.ordermanagement.model.ShopStatus;
import com.example.ordermanagement.model.ShopType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShopServiceInterface {
    
    // Basic CRUD operations
    ShopResponse createShop(ShopRequest request);
    
    ShopResponse getShopById(Long id);
    
    List<ShopResponse> getAllShops();
    
    Page<ShopResponse> getAllShops(Pageable pageable);
    
    ShopResponse updateShop(Long id, ShopRequest request);
    
    void deleteShop(Long id);
    
    // Search and filter operations
    List<ShopResponse> searchShopsByName(String shopName);
    
    List<ShopResponse> getShopsByOwnerName(String ownerName);
    
    List<ShopResponse> getShopsByStatus(ShopStatus status);
    
    Page<ShopResponse> getShopsByStatus(ShopStatus status, Pageable pageable);
    
    List<ShopResponse> getShopsByType(ShopType shopType);
    
    Page<ShopResponse> getShopsByType(ShopType shopType, Pageable pageable);
    
    List<ShopResponse> getShopsByCity(String city);
    
    Page<ShopResponse> getShopsByCity(String city, Pageable pageable);
    
    List<ShopResponse> getShopsByState(String state);
    
    List<ShopResponse> getShopsByCountry(String country);
    
    List<ShopResponse> getShopsByLocation(String city, String state);
    
    // Status management operations
    ShopResponse approveShop(Long id);
    
    ShopResponse suspendShop(Long id);
    
    ShopResponse activateShop(Long id);
    
    ShopResponse updateShopStatus(Long id, ShopStatus status);
    
    List<ShopResponse> getPendingShops();
    
    List<ShopResponse> getActiveShops();
    
    // Advanced search
    List<ShopResponse> searchShops(String shopName, String ownerName, String city, 
                                 ShopStatus status, ShopType shopType);
    
    Page<ShopResponse> searchShops(String query, Pageable pageable);
    
    // Statistics and analytics
    long getTotalShopsCount();
    
    long getTotalShops();
    
    long getShopsCountByStatus(ShopStatus status);
    
    long getShopCountByStatus(ShopStatus status);
    
    long getShopsCountByType(ShopType shopType);
    
    long getShopCountByType(ShopType shopType);
    
    List<ShopResponse> getRecentShops(int limit);
    
    List<ShopResponse> getShopsWithMenuItems();
    
    List<ShopResponse> getShopsWithoutMenuItems();
    
    // Validation operations
    boolean isEmailAvailable(String email);
    
    boolean isShopNameAvailableInCity(String shopName, String city);
    
    ShopResponse getShopByEmail(String email);
}
