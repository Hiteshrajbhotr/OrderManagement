package com.example.ordermanagement.service;

import com.example.ordermanagement.dto.ShopRequest;
import com.example.ordermanagement.dto.ShopResponse;
import com.example.ordermanagement.model.Shop;
import com.example.ordermanagement.model.ShopStatus;
import com.example.ordermanagement.model.ShopType;
import com.example.ordermanagement.repository.ShopRepository;
import com.example.ordermanagement.repository.MenuItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShopService implements ShopServiceInterface {
    
    private static final Logger logger = LoggerFactory.getLogger(ShopService.class);
    
    private final ShopRepository shopRepository;
    private final MenuItemRepository menuItemRepository;
    
    @Autowired
    public ShopService(ShopRepository shopRepository, MenuItemRepository menuItemRepository) {
        this.shopRepository = shopRepository;
        this.menuItemRepository = menuItemRepository;
    }
    
    @Override
    public ShopResponse createShop(ShopRequest request) {
        // Validate email uniqueness
        if (shopRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        
        // Validate shop name uniqueness in the same city
        if (shopRepository.existsByShopNameAndCity(request.getShopName(), request.getCity())) {
            throw new RuntimeException("Shop name already exists in " + request.getCity());
        }
        
        Shop shop = new Shop();
        shop.setShopName(request.getShopName());
        shop.setOwnerName(request.getOwnerName());
        shop.setEmail(request.getEmail());
        shop.setPhoneNumber(request.getPhoneNumber());
        shop.setCity(request.getCity());
        shop.setState(request.getState());
        shop.setCountry(request.getCountry());
        shop.setPincode(request.getPincode());
        shop.setAddress(request.getAddress());
        shop.setDescription(request.getDescription());
        shop.setShopType(request.getShopType());
        shop.setStatus(ShopStatus.PENDING); // New shops start as pending
        shop.setRegistrationDate(LocalDateTime.now());
        
        Shop savedShop = shopRepository.save(shop);
        return convertToResponse(savedShop);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ShopResponse getShopById(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + id));
        return convertToResponse(shop);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ShopResponse> getAllShops() {
        return shopRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public ShopResponse updateShop(Long id, ShopRequest request) {
        Shop existingShop = shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + id));
        
        // Check email uniqueness (excluding current shop)
        if (!existingShop.getEmail().equals(request.getEmail()) && 
            shopRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        
        // Check shop name uniqueness in city (excluding current shop)
        if ((!existingShop.getShopName().equals(request.getShopName()) || 
             !existingShop.getCity().equals(request.getCity())) &&
            shopRepository.existsByShopNameAndCity(request.getShopName(), request.getCity())) {
            throw new RuntimeException("Shop name already exists in " + request.getCity());
        }
        
        existingShop.setShopName(request.getShopName());
        existingShop.setOwnerName(request.getOwnerName());
        existingShop.setEmail(request.getEmail());
        existingShop.setPhoneNumber(request.getPhoneNumber());
        existingShop.setCity(request.getCity());
        existingShop.setState(request.getState());
        existingShop.setCountry(request.getCountry());
        existingShop.setPincode(request.getPincode());
        existingShop.setAddress(request.getAddress());
        existingShop.setDescription(request.getDescription());
        existingShop.setShopType(request.getShopType());
        
        Shop updatedShop = shopRepository.save(existingShop);
        return convertToResponse(updatedShop);
    }
    
    @Override
    public void deleteShop(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + id));
        
        // Delete all menu items first
        menuItemRepository.deleteByShopId(id);
        
        // Then delete the shop
        shopRepository.delete(shop);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ShopResponse> searchShopsByName(String shopName) {
        return shopRepository.findByShopNameContainingIgnoreCase(shopName).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ShopResponse> getShopsByOwnerName(String ownerName) {
        return shopRepository.findByOwnerNameContainingIgnoreCase(ownerName).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ShopResponse> getShopsByStatus(ShopStatus status) {
        return shopRepository.findByStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ShopResponse> getShopsByType(ShopType shopType) {
        return shopRepository.findByShopType(shopType).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ShopResponse> getShopsByCity(String city) {
        return shopRepository.findByCity(city).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ShopResponse> getShopsByState(String state) {
        return shopRepository.findByState(state).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ShopResponse> getShopsByCountry(String country) {
        return shopRepository.findByCountry(country).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ShopResponse> getShopsByLocation(String city, String state) {
        return shopRepository.findByLocation(city, state).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public ShopResponse approveShop(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + id));
        
        if (shop.getStatus() != ShopStatus.PENDING) {
            throw new RuntimeException("Only pending shops can be approved");
        }
        
        shop.setStatus(ShopStatus.ACTIVE);
        Shop updatedShop = shopRepository.save(shop);
        return convertToResponse(updatedShop);
    }
    
    @Override
    public ShopResponse suspendShop(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + id));
        
        shop.setStatus(ShopStatus.SUSPENDED);
        Shop updatedShop = shopRepository.save(shop);
        return convertToResponse(updatedShop);
    }
    
    @Override
    public ShopResponse activateShop(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + id));
        
        shop.setStatus(ShopStatus.ACTIVE);
        Shop updatedShop = shopRepository.save(shop);
        return convertToResponse(updatedShop);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ShopResponse> getPendingShops() {
        return shopRepository.findPendingShops().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ShopResponse> getActiveShops() {
        return shopRepository.findActiveShops().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ShopResponse> searchShops(String shopName, String ownerName, String city, 
                                        ShopStatus status, ShopType shopType) {
        return shopRepository.searchShops(shopName, ownerName, city, status, shopType).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalShopsCount() {
        return shopRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getShopsCountByStatus(ShopStatus status) {
        return shopRepository.countByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getShopsCountByType(ShopType shopType) {
        return shopRepository.countByShopType(shopType);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ShopResponse> getRecentShops(int limit) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return shopRepository.findShopsRegisteredAfter(thirtyDaysAgo).stream()
                .limit(limit)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ShopResponse> getShopsWithMenuItems() {
        return shopRepository.findShopsWithMenuItems().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ShopResponse> getShopsWithoutMenuItems() {
        return shopRepository.findShopsWithoutMenuItems().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !shopRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isShopNameAvailableInCity(String shopName, String city) {
        return !shopRepository.existsByShopNameAndCity(shopName, city);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ShopResponse getShopByEmail(String email) {
        Shop shop = shopRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Shop not found with email: " + email));
        return convertToResponse(shop);
    }
    
    // Helper method to convert Shop entity to ShopResponse DTO
    private ShopResponse convertToResponse(Shop shop) {
        ShopResponse response = new ShopResponse(
                shop.getId(),
                shop.getShopName(),
                shop.getOwnerName(),
                shop.getEmail(),
                shop.getPhoneNumber(),
                shop.getCity(),
                shop.getState(),
                shop.getCountry(),
                shop.getPincode(),
                shop.getAddress(),
                shop.getDescription(),
                shop.getShopType(),
                shop.getStatus(),
                shop.getRegistrationDate(),
                shop.getCreatedAt(),
                shop.getUpdatedAt()
        );
        
        // Set menu items count
        long menuItemsCount = menuItemRepository.countByShopId(shop.getId());
        response.setMenuItemsCount((int) menuItemsCount);
        
        return response;
    }
    
    // Additional methods required by interface
    @Override
    @Transactional(readOnly = true)
    public Page<ShopResponse> getAllShops(Pageable pageable) {
        logger.info("getAllShops called with page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Shop> shopPage = shopRepository.findAll(pageable);
        logger.info("Repository returned {} total shops, {} in current page", shopPage.getTotalElements(), shopPage.getContent().size());
        
        for (Shop shop : shopPage.getContent()) {
            logger.debug("Shop from DB: {} (ID: {})", shop.getShopName(), shop.getId());
        }
        
        Page<ShopResponse> result = shopPage.map(shop -> {
            try {
                ShopResponse response = convertToResponse(shop);
                logger.debug("Converted: {}", response.getShopName());
                return response;
            } catch (Exception e) {
                logger.error("Error converting shop {}: {}", shop.getShopName(), e.getMessage());
                e.printStackTrace();
                return null;
            }
        });
        
        logger.info("Final result: {} total, {} in page", result.getTotalElements(), result.getContent().size());
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ShopResponse> getShopsByStatus(ShopStatus status, Pageable pageable) {
        return shopRepository.findByStatus(status, pageable)
                .map(this::convertToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ShopResponse> getShopsByType(ShopType shopType, Pageable pageable) {
        return shopRepository.findByShopType(shopType, pageable)
                .map(this::convertToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ShopResponse> getShopsByCity(String city, Pageable pageable) {
        return shopRepository.findByCity(city, pageable)
                .map(this::convertToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ShopResponse> searchShops(String query, Pageable pageable) {
        return shopRepository.findByShopNameContainingIgnoreCaseOrOwnerNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                query, query, query, pageable)
                .map(this::convertToResponse);
    }
    
    @Override
    public ShopResponse updateShopStatus(Long id, ShopStatus status) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + id));
        
        shop.setStatus(status);
        Shop updatedShop = shopRepository.save(shop);
        return convertToResponse(updatedShop);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalShops() {
        return shopRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getShopCountByStatus(ShopStatus status) {
        return shopRepository.countByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getShopCountByType(ShopType shopType) {
        return shopRepository.countByShopType(shopType);
    }
}
