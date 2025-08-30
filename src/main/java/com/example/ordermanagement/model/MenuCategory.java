package com.example.ordermanagement.model;

public enum MenuCategory {
    APPETIZER("Appetizer"),
    MAIN_COURSE("Main Course"),
    DESSERT("Dessert"),
    BEVERAGE("Beverage"),
    SNACK("Snack"),
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner"),
    SALAD("Salad"),
    SOUP("Soup"),
    PIZZA("Pizza"),
    BURGER("Burger"),
    SANDWICH("Sandwich"),
    PASTA("Pasta"),
    SEAFOOD("Seafood"),
    VEGETARIAN("Vegetarian"),
    VEGAN("Vegan"),
    BAKERY("Bakery Items"),
    ICE_CREAM("Ice Cream"),
    COFFEE("Coffee"),
    TEA("Tea"),
    JUICE("Juice"),
    OTHER("Other");
    
    private final String displayName;
    
    MenuCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
