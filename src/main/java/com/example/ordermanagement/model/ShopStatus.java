package com.example.ordermanagement.model;

public enum ShopStatus {
    PENDING("Pending Approval"),
    ACTIVE("Active"),
    SUSPENDED("Suspended"),
    INACTIVE("Inactive");
    
    private final String displayName;
    
    ShopStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
