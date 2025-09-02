package com.example.ordermanagement.service;

import com.example.ordermanagement.dto.MenuItemRequest;
import com.example.ordermanagement.dto.MenuItemResponse;
import com.example.ordermanagement.model.MenuItem;
import com.example.ordermanagement.model.MenuCategory;
import com.example.ordermanagement.model.Shop;
import com.example.ordermanagement.repository.MenuItemRepository;
import com.example.ordermanagement.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuItemService {
    
    private final MenuItemRepository menuItemRepository;
    private final ShopRepository shopRepository;
    
    @Autowired
    public MenuItemService(MenuItemRepository menuItemRepository, ShopRepository shopRepository) {
        this.menuItemRepository = menuItemRepository;
        this.shopRepository = shopRepository;
    }
    
    public MenuItemResponse createMenuItem(MenuItemRequest request) {
        // Validate shop exists
        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + request.getShopId()));
        
        // Validate item name uniqueness within the shop
        if (menuItemRepository.existsByShopIdAndItemName(request.getShopId(), request.getItemName())) {
            throw new RuntimeException("Menu item with name '" + request.getItemName() + 
                                     "' already exists in this shop");
        }
        
        MenuItem menuItem = new MenuItem();
        menuItem.setItemName(request.getItemName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setCategory(request.getCategory());
        menuItem.setIsAvailable(request.getIsAvailable());
        menuItem.setIsVegetarian(request.getIsVegetarian());
        menuItem.setIsVegan(request.getIsVegan());
        menuItem.setPreparationTimeMinutes(request.getPreparationTimeMinutes());
        menuItem.setImageUrl(request.getImageUrl());
        menuItem.setShop(shop);
        
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return convertToResponse(savedMenuItem);
    }
    
    @Transactional(readOnly = true)
    public MenuItemResponse getMenuItemById(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));
        return convertToResponse(menuItem);
    }
    
    @Transactional(readOnly = true)
    public MenuItem getMenuItemEntityById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getAllMenuItems() {
        return menuItemRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItemsByShopId(Long shopId) {
        return menuItemRepository.findByShopId(shopId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getAvailableMenuItemsByShopId(Long shopId) {
        return menuItemRepository.findAvailableItemsByShopId(shopId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public MenuItemResponse updateMenuItem(Long id, MenuItemRequest request) {
        MenuItem existingMenuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));
        
        // Validate shop exists
        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + request.getShopId()));
        
        // Check item name uniqueness within the shop (excluding current item)
        if (!existingMenuItem.getItemName().equals(request.getItemName()) &&
            menuItemRepository.existsByShopIdAndItemName(request.getShopId(), request.getItemName())) {
            throw new RuntimeException("Menu item with name '" + request.getItemName() + 
                                     "' already exists in this shop");
        }
        
        existingMenuItem.setItemName(request.getItemName());
        existingMenuItem.setDescription(request.getDescription());
        existingMenuItem.setPrice(request.getPrice());
        existingMenuItem.setCategory(request.getCategory());
        existingMenuItem.setIsAvailable(request.getIsAvailable());
        existingMenuItem.setIsVegetarian(request.getIsVegetarian());
        existingMenuItem.setIsVegan(request.getIsVegan());
        existingMenuItem.setPreparationTimeMinutes(request.getPreparationTimeMinutes());
        existingMenuItem.setImageUrl(request.getImageUrl());
        existingMenuItem.setShop(shop);
        
        MenuItem updatedMenuItem = menuItemRepository.save(existingMenuItem);
        return convertToResponse(updatedMenuItem);
    }
    
    public void deleteMenuItem(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));
        menuItemRepository.delete(menuItem);
    }
    
    @Transactional(readOnly = true)
    public List<MenuItemResponse> searchMenuItemsByName(String itemName) {
        return menuItemRepository.findByItemNameContainingIgnoreCase(itemName).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItemsByCategory(MenuCategory category) {
        return menuItemRepository.findByCategory(category).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItemsByShopAndCategory(Long shopId, MenuCategory category) {
        return menuItemRepository.findByShopIdAndCategory(shopId, category).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getVegetarianItemsByShop(Long shopId) {
        return menuItemRepository.findVegetarianItemsByShopId(shopId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getVeganItemsByShop(Long shopId) {
        return menuItemRepository.findVeganItemsByShopId(shopId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItemsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return menuItemRepository.findByPriceRange(minPrice, maxPrice).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItemsByShopAndPriceRange(Long shopId, BigDecimal minPrice, BigDecimal maxPrice) {
        return menuItemRepository.findByShopIdAndPriceRange(shopId, minPrice, maxPrice).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getQuickItemsByShop(Long shopId, Integer maxPreparationTime) {
        return menuItemRepository.findQuickItemsByShopId(shopId, maxPreparationTime).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MenuItemResponse> searchMenuItems(Long shopId, String itemName, MenuCategory category,
                                                Boolean isAvailable, Boolean isVegetarian, Boolean isVegan,
                                                BigDecimal minPrice, BigDecimal maxPrice) {
        return menuItemRepository.searchMenuItems(shopId, itemName, category, isAvailable, 
                                                isVegetarian, isVegan, minPrice, maxPrice).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public MenuItemResponse toggleAvailability(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));
        
        menuItem.setIsAvailable(!menuItem.getIsAvailable());
        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);
        return convertToResponse(updatedMenuItem);
    }
    
    @Transactional(readOnly = true)
    public long getMenuItemsCountByShop(Long shopId) {
        return menuItemRepository.countByShopId(shopId);
    }
    
    @Transactional(readOnly = true)
    public long getAvailableMenuItemsCountByShop(Long shopId) {
        return menuItemRepository.countAvailableByShopId(shopId);
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getAveragePriceByShop(Long shopId) {
        return menuItemRepository.getAveragePriceByShopId(shopId);
    }
    
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMostExpensiveItemsByShop(Long shopId) {
        return menuItemRepository.findItemsByShopIdOrderByPriceDesc(shopId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getCheapestItemsByShop(Long shopId) {
        return menuItemRepository.findItemsByShopIdOrderByPriceAsc(shopId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getNewestItemsByShop(Long shopId) {
        return menuItemRepository.findItemsByShopIdOrderByCreatedAtDesc(shopId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // Helper method to convert MenuItem entity to MenuItemResponse DTO
    private MenuItemResponse convertToResponse(MenuItem menuItem) {
        return new MenuItemResponse(
                menuItem.getId(),
                menuItem.getItemName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getCategory(),
                menuItem.getIsAvailable(),
                menuItem.getIsVegetarian(),
                menuItem.getIsVegan(),
                menuItem.getPreparationTimeMinutes(),
                menuItem.getImageUrl(),
                menuItem.getCreatedAt(),
                menuItem.getUpdatedAt(),
                menuItem.getShop().getId(),
                menuItem.getShop().getShopName()
        );
    }
    
    // Additional method for getting menu items by shop ID
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItemsByShop(Long shopId) {
        return menuItemRepository.findByShopId(shopId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // Method to get all available menu items as entities (for customer dashboard)
    @Transactional(readOnly = true)
    public List<MenuItem> getAllAvailableMenuItems() {
        return menuItemRepository.findByIsAvailableTrue();
    }
}
