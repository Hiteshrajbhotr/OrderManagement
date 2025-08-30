package com.example.ordermanagement.model;

public enum ShopType {
    RESTAURANT("Restaurant"),
    CAFE("Cafe"),
    BAKERY("Bakery"),
    FAST_FOOD("Fast Food"),
    GROCERY("Grocery Store"),
    ELECTRONICS("Electronics"),
    CLOTHING("Clothing"),
    PHARMACY("Pharmacy"),
    BOOKSTORE("Bookstore"),
    OTHER("Other");
    
    private final String displayName;
    
    ShopType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
