package com.example.ordermanagement.controller;

import com.example.ordermanagement.model.Cart;
import com.example.ordermanagement.model.CartItem;
import com.example.ordermanagement.model.MenuItem;
import com.example.ordermanagement.model.User;
import com.example.ordermanagement.service.CartService;
import com.example.ordermanagement.service.MenuItemService;
import com.example.ordermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/customer")
public class CustomerDashboardController {

    @Autowired
    private CartService cartService;
    
    @Autowired
    private MenuItemService menuItemService;
    
    @Autowired
    private UserService userService;

    // Customer Dashboard for logged-in customers
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public String customerDashboard(Model model, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            
            // Get all available menu items
            List<MenuItem> menuItems = menuItemService.getAllAvailableMenuItems();
            
            // Get customer's cart
            Cart cart = cartService.getOrCreateCart(user);
            List<CartItem> cartItems = cart.getCartItems();
            
            // Calculate cart statistics
            int totalCartItems = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
            BigDecimal cartTotal = cart.getTotalAmount();
            
            // Add attributes to model
            model.addAttribute("menuItems", menuItems);
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("cartTotal", cartTotal != null ? "$" + cartTotal.toString() : "$0.00");
            model.addAttribute("totalCartItems", totalCartItems);
            model.addAttribute("totalOrders", 0); // TODO: Implement order counting
            
            return "customer/dashboard";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading dashboard: " + e.getMessage());
            return "customer/dashboard";
        }
    }

    // Cart Management Endpoints
    @PostMapping("/cart/add")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseBody
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> request, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            
            Long menuItemId = Long.valueOf(request.get("menuItemId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            
            MenuItem menuItem = menuItemService.getMenuItemEntityById(menuItemId);
            cartService.addItemToCart(user, menuItem, quantity);
            
            return ResponseEntity.ok().body(Map.of("success", true, "message", "Item added to cart"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/cart/update/{cartItemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseBody
    public ResponseEntity<?> updateCartQuantity(@PathVariable Long cartItemId, 
                                              @RequestBody Map<String, Object> request, 
                                              Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            cartService.updateCartItemQuantity(user, cartItemId, quantity);
            
            return ResponseEntity.ok().body(Map.of("success", true, "message", "Cart updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/cart/remove/{cartItemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseBody
    public ResponseEntity<?> removeFromCart(@PathVariable Long cartItemId, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            cartService.removeItemFromCart(user, cartItemId);
            
            return ResponseEntity.ok().body(Map.of("success", true, "message", "Item removed from cart"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/cart/clear")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseBody
    public ResponseEntity<?> clearCart(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            
            cartService.clearCart(user);
            
            return ResponseEntity.ok().body(Map.of("success", true, "message", "Cart cleared"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/cart/count")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseBody
    public ResponseEntity<?> getCartCount(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            Cart cart = cartService.getOrCreateCart(user);
            
            int totalItems = cart.getCartItems().stream().mapToInt(CartItem::getQuantity).sum();
            BigDecimal totalAmount = cart.getTotalAmount();
            
            return ResponseEntity.ok().body(Map.of(
                "totalItems", totalItems,
                "totalAmount", totalAmount != null ? totalAmount.toString() : "0.00",
                "formattedTotal", totalAmount != null ? "$" + totalAmount.toString() : "$0.00"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/cart/data")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseBody
    public ResponseEntity<?> getCartData(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            Cart cart = cartService.getOrCreateCart(user);
            
            List<CartItem> cartItems = cart.getCartItems();
            int totalItems = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
            BigDecimal totalAmount = cart.getTotalAmount();
            
            // Create cart items data for frontend
            List<Map<String, Object>> cartItemsData = cartItems.stream().map(item -> {
                Map<String, Object> itemData = new java.util.HashMap<>();
                itemData.put("id", item.getId());
                itemData.put("menuItemId", item.getMenuItem().getId());
                itemData.put("itemName", item.getMenuItem().getItemName());
                itemData.put("imageUrl", item.getMenuItem().getImageUrl() != null ? item.getMenuItem().getImageUrl() : "/images/default-food.jpg");
                itemData.put("unitPrice", item.getMenuItem().getPrice().toString());
                itemData.put("formattedUnitPrice", "$" + item.getMenuItem().getPrice().toString());
                itemData.put("quantity", item.getQuantity());
                itemData.put("subtotal", item.getSubtotal().toString());
                itemData.put("formattedSubtotal", "$" + item.getSubtotal().toString());
                return itemData;
            }).toList();
            
            return ResponseEntity.ok().body(Map.of(
                "cartItems", cartItemsData,
                "totalItems", totalItems,
                "totalAmount", totalAmount != null ? totalAmount.toString() : "0.00",
                "formattedTotal", totalAmount != null ? "$" + totalAmount.toString() : "$0.00",
                "isEmpty", cartItems.isEmpty()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/checkout")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String checkout(Model model, Authentication authentication) {
        // TODO: Implement checkout functionality
        return "customer/checkout";
    }
}
