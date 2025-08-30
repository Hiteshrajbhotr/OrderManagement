package com.example.ordermanagement.dto;

import jakarta.validation.constraints.*;
import com.example.ordermanagement.model.MenuCategory;

import java.math.BigDecimal;

public class MenuItemRequest {
    
    @NotBlank(message = "Item name is required")
    @Size(max = 100, message = "Item name must not exceed 100 characters")
    private String itemName;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price must be a valid amount")
    private BigDecimal price;
    
    @NotNull(message = "Category is required")
    private MenuCategory category;
    
    private Boolean isAvailable = true;
    
    private Boolean isVegetarian = false;
    
    private Boolean isVegan = false;
    
    @Min(value = 1, message = "Preparation time must be at least 1 minute")
    @Max(value = 300, message = "Preparation time cannot exceed 300 minutes")
    private Integer preparationTimeMinutes;
    
    @Size(max = 200, message = "Image URL must not exceed 200 characters")
    private String imageUrl;
    
    @NotNull(message = "Shop ID is required")
    private Long shopId;
    
    // Constructors
    public MenuItemRequest() {}
    
    public MenuItemRequest(String itemName, String description, BigDecimal price, 
                          MenuCategory category, Long shopId) {
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.category = category;
        this.shopId = shopId;
    }
    
    // Getters and Setters
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
    
    public Long getShopId() {
        return shopId;
    }
    
    public void setShopId(Long shopId) {
        this.shopId = shopId;
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
    
    @Override
    public String toString() {
        return "MenuItemRequest{" +
                "itemName='" + itemName + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", isAvailable=" + isAvailable +
                ", shopId=" + shopId +
                '}';
    }
}
