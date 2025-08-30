package com.example.ordermanagement.controller;

import com.example.ordermanagement.dto.ShopRequest;
import com.example.ordermanagement.dto.ShopResponse;
import com.example.ordermanagement.model.ShopStatus;
import com.example.ordermanagement.model.ShopType;
import com.example.ordermanagement.service.ShopServiceInterface;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shops")
@CrossOrigin(origins = "*")
public class ShopApiController {

    @Autowired
    private ShopServiceInterface shopService;

    /**
     * Get all shops with pagination
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public ResponseEntity<Map<String, Object>> getAllShops(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "registrationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<ShopResponse> shops = shopService.getAllShops(pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("shops", shops.getContent());
            response.put("currentPage", shops.getNumber());
            response.put("totalItems", shops.getTotalElements());
            response.put("totalPages", shops.getTotalPages());
            response.put("hasNext", shops.hasNext());
            response.put("hasPrevious", shops.hasPrevious());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve shops");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get shop by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public ResponseEntity<ShopResponse> getShopById(@PathVariable Long id) {
        try {
            ShopResponse shop = shopService.getShopById(id);
            return ResponseEntity.ok(shop);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create new shop
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createShop(@Valid @RequestBody ShopRequest shopRequest) {
        try {
            ShopResponse createdShop = shopService.createShop(shopRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Shop created successfully");
            response.put("shop", createdShop);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create shop");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Update shop
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateShop(@PathVariable Long id, 
                                                        @Valid @RequestBody ShopRequest shopRequest) {
        try {
            ShopResponse updatedShop = shopService.updateShop(id, shopRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Shop updated successfully");
            response.put("shop", updatedShop);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update shop");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Update shop status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateShopStatus(@PathVariable Long id, 
                                                              @RequestParam ShopStatus status) {
        try {
            ShopResponse updatedShop = shopService.updateShopStatus(id, status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Shop status updated successfully");
            response.put("shop", updatedShop);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update shop status");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Delete shop
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteShop(@PathVariable Long id) {
        try {
            shopService.deleteShop(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Shop deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete shop");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Search shops
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public ResponseEntity<Map<String, Object>> searchShops(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("shopName").ascending());
            Page<ShopResponse> shops = shopService.searchShops(query, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("shops", shops.getContent());
            response.put("currentPage", shops.getNumber());
            response.put("totalItems", shops.getTotalElements());
            response.put("totalPages", shops.getTotalPages());
            response.put("query", query);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to search shops");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get shops by city
     */
    @GetMapping("/city/{city}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public ResponseEntity<Map<String, Object>> getShopsByCity(
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("shopName").ascending());
            Page<ShopResponse> shops = shopService.getShopsByCity(city, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("shops", shops.getContent());
            response.put("currentPage", shops.getNumber());
            response.put("totalItems", shops.getTotalElements());
            response.put("totalPages", shops.getTotalPages());
            response.put("city", city);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve shops by city");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get shops by type
     */
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public ResponseEntity<Map<String, Object>> getShopsByType(
            @PathVariable ShopType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("shopName").ascending());
            Page<ShopResponse> shops = shopService.getShopsByType(type, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("shops", shops.getContent());
            response.put("currentPage", shops.getNumber());
            response.put("totalItems", shops.getTotalElements());
            response.put("totalPages", shops.getTotalPages());
            response.put("type", type);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve shops by type");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get shops by status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getShopsByStatus(
            @PathVariable ShopStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("registrationDate").descending());
            Page<ShopResponse> shops = shopService.getShopsByStatus(status, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("shops", shops.getContent());
            response.put("currentPage", shops.getNumber());
            response.put("totalItems", shops.getTotalElements());
            response.put("totalPages", shops.getTotalPages());
            response.put("status", status);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve shops by status");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get shop statistics
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getShopStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Get total shops
            long totalShops = shopService.getTotalShops();
            stats.put("totalShops", totalShops);
            
            // Get shops by status
            Map<ShopStatus, Long> shopsByStatus = new HashMap<>();
            for (ShopStatus status : ShopStatus.values()) {
                long count = shopService.getShopCountByStatus(status);
                shopsByStatus.put(status, count);
            }
            stats.put("shopsByStatus", shopsByStatus);
            
            // Get shops by type
            Map<ShopType, Long> shopsByType = new HashMap<>();
            for (ShopType type : ShopType.values()) {
                long count = shopService.getShopCountByType(type);
                shopsByType.put(type, count);
            }
            stats.put("shopsByType", shopsByType);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve shop statistics");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get available shop types
     */
    @GetMapping("/types")
    public ResponseEntity<ShopType[]> getShopTypes() {
        return ResponseEntity.ok(ShopType.values());
    }

    /**
     * Get available shop statuses
     */
    @GetMapping("/statuses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShopStatus[]> getShopStatuses() {
        return ResponseEntity.ok(ShopStatus.values());
    }
}
