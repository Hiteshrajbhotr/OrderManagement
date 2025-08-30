package com.example.ordermanagement.model;

public enum Role {
    ADMIN("ROLE_ADMIN", "Administrator"),
    SHOP("ROLE_SHOP", "Shop"),
    CUSTOMER("ROLE_CUSTOMER", "Customer");

    private final String authority;
    private final String displayName;

    Role(String authority, String displayName) {
        this.authority = authority;
        this.displayName = displayName;
    }

    public String getAuthority() {
        return authority;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
