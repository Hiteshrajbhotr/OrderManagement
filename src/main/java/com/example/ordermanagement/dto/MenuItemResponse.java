package com.example.ordermanagement.dto;

import com.example.ordermanagement.model.MenuCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MenuItemResponse {
    
    private Long id;
    private String itemName;
    private String description;
    private BigDecimal price;
    private MenuCategory category;
    private Boolean isAvailable;
    private Boolean isVegetarian;
    private Boolean isVegan;
    private Integer preparationTimeMinutes;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long shopId;
    private String shopName;
    
    // Constructors
    public MenuItemResponse() {}
    
    public MenuItemResponse(Long id, String itemName, String description, BigDecimal price,
                           MenuCategory category, Boolean isAvailable, Boolean isVegetarian,
                           Boolean isVegan, Integer preparationTimeMinutes, String imageUrl,
                           LocalDateTime createdAt, LocalDateTime updatedAt, Long shopId, String shopName) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.category = category;
        this.isAvailable = isAvailable;
        this.isVegetarian = isVegetarian;
        this.isVegan = isVegan;
        this.preparationTimeMinutes = preparationTimeMinutes;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.shopId = shopId;
        this.shopName = shopName;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public MenuCategory getCategory() {
        return category;
    }
    
    public void setCategory(MenuCategory category) {
        this.category = category;
    }
    
    public Boolean getIsAvailable() {
        return isAvailable;
    }
    
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
    
    public Boolean getIsVegetarian() {
        return isVegetarian;
    }
    
    public void setIsVegetarian(Boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }
    
    public Boolean getIsVegan() {
        return isVegan;
    }
    
    public void setIsVegan(Boolean isVegan) {
        this.isVegan = isVegan;
    }
    
    public Integer getPreparationTimeMinutes() {
        return preparationTimeMinutes;
    }
    
    public void setPreparationTimeMinutes(Integer preparationTimeMinutes) {
        this.preparationTimeMinutes = preparationTimeMinutes;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Long getShopId() {
        return shopId;
    }
    
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
    
    public String getShopName() {
        return shopName;
    }
    
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
    
    // Helper methods
    public String getFormattedPrice() {
        return price != null ? "$" + price.toString() : "$0.00";
    }
    
    public String getDietaryInfo() {
        if (Boolean.TRUE.equals(isVegan)) return "Vegan";
        if (Boolean.TRUE.equals(isVegetarian)) return "Vegetarian";
        return "Non-Vegetarian";
    }
    
    public String getAvailabilityStatus() {
        return Boolean.TRUE.equals(isAvailable) ? "Available" : "Out of Stock";
    }
    
    public String getCategoryDisplayName() {
        return category != null ? category.getDisplayName() : "Unknown";
    }
    
    public String getPreparationTimeDisplay() {
        return preparationTimeMinutes != null ? preparationTimeMinutes + " mins" : "N/A";
    }
    
    @Override
    public String toString() {
        return "MenuItemResponse{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", isAvailable=" + isAvailable +
                ", shopName='" + shopName + '\'' +
                '}';
    }
}
