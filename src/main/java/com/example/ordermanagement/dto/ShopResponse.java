package com.example.ordermanagement.dto;

import com.example.ordermanagement.model.ShopType;
import com.example.ordermanagement.model.ShopStatus;

import java.time.LocalDateTime;

public class ShopResponse {
    
    private Long id;
    private String shopName;
    private String ownerName;
    private String email;
    private String phoneNumber;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private String address;
    private String description;
    private ShopType shopType;
    private ShopStatus status;
    private LocalDateTime registrationDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int menuItemsCount;
    
    // Constructors
    public ShopResponse() {}
    
    public ShopResponse(Long id, String shopName, String ownerName, String email, 
                       String phoneNumber, String city, String state, String country, 
                       String pincode, String address, String description, ShopType shopType, 
                       ShopStatus status, LocalDateTime registrationDate, 
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.shopName = shopName;
        this.ownerName = ownerName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
        this.address = address;
        this.description = description;
        this.shopType = shopType;
        this.status = status;
        this.registrationDate = registrationDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getShopName() {
        return shopName;
    }
    
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getPincode() {
        return pincode;
    }
    
    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public ShopType getShopType() {
        return shopType;
    }
    
    public void setShopType(ShopType shopType) {
        this.shopType = shopType;
    }
    
    public ShopStatus getStatus() {
        return status;
    }
    
    public void setStatus(ShopStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
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
    
    public int getMenuItemsCount() {
        return menuItemsCount;
    }
    
    public void setMenuItemsCount(int menuItemsCount) {
        this.menuItemsCount = menuItemsCount;
    }
    
    // Helper methods
    public String getFullAddress() {
        return city + ", " + state + ", " + country + " - " + pincode;
    }
    
    public String getShortAddress() {
        return city + ", " + state;
    }
    
    public String getStatusDisplayName() {
        return status != null ? status.getDisplayName() : "Unknown";
    }
    
    public String getShopTypeDisplayName() {
        return shopType != null ? shopType.getDisplayName() : "Unknown";
    }
    
    public boolean isActive() {
        return status == ShopStatus.ACTIVE;
    }
    
    public boolean isPending() {
        return status == ShopStatus.PENDING;
    }
    
    @Override
    public String toString() {
        return "ShopResponse{" +
                "id=" + id +
                ", shopName='" + shopName + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", shopType=" + shopType +
                ", status=" + status +
                ", menuItemsCount=" + menuItemsCount +
                '}';
    }
}
