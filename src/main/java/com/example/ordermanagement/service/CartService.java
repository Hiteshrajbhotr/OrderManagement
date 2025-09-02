package com.example.ordermanagement.service;

import com.example.ordermanagement.model.Cart;
import com.example.ordermanagement.model.CartItem;
import com.example.ordermanagement.model.MenuItem;
import com.example.ordermanagement.model.User;
import com.example.ordermanagement.repository.CartRepository;
import com.example.ordermanagement.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    /**
     * Get or create cart for user
     */
    public Cart getOrCreateCart(User user) {
        Optional<Cart> existingCart = cartRepository.findByUserWithItems(user);
        if (existingCart.isPresent()) {
            return existingCart.get();
        }
        
        Cart newCart = new Cart(user);
        return cartRepository.save(newCart);
    }
    
    /**
     * Add item to cart
     */
    public Cart addItemToCart(User user, MenuItem menuItem, Integer quantity) {
        Cart cart = getOrCreateCart(user);
        
        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndMenuItem(cart, menuItem);
        
        if (existingItem.isPresent()) {
            // Update quantity if item already exists
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.calculateSubtotal();
            cartItemRepository.save(cartItem);
        } else {
            // Add new item to cart
            CartItem cartItem = new CartItem(cart, menuItem, quantity);
            cart.addCartItem(cartItem);
            cartItemRepository.save(cartItem);
        }
        
        cart.updateTotals();
        return cartRepository.save(cart);
    }
    
    /**
     * Update cart item quantity
     */
    public Cart updateCartItemQuantity(User user, Long cartItemId, Integer quantity) {
        Cart cart = getOrCreateCart(user);
        
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(cartItemId);
        if (cartItemOpt.isPresent()) {
            CartItem cartItem = cartItemOpt.get();
            
            // Verify the cart item belongs to the user's cart
            if (cartItem.getCart().getUser().getId().equals(user.getId())) {
                if (quantity <= 0) {
                    // Remove item if quantity is 0 or negative
                    cart.removeCartItem(cartItem);
                    cartItemRepository.delete(cartItem);
                } else {
                    // Update quantity
                    cartItem.setQuantity(quantity);
                    cartItem.calculateSubtotal();
                    cartItemRepository.save(cartItem);
                }
                
                cart.updateTotals();
                cartRepository.save(cart);
            }
        }
        
        return cart;
    }
    
    /**
     * Remove item from cart
     */
    public Cart removeItemFromCart(User user, Long cartItemId) {
        Cart cart = getOrCreateCart(user);
        
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(cartItemId);
        if (cartItemOpt.isPresent()) {
            CartItem cartItem = cartItemOpt.get();
            
            // Verify the cart item belongs to the user's cart
            if (cartItem.getCart().getUser().getId().equals(user.getId())) {
                cart.removeCartItem(cartItem);
                cartItemRepository.delete(cartItem);
                cart.updateTotals();
                cartRepository.save(cart);
            }
        }
        
        return cart;
    }
    
    /**
     * Clear all items from cart
     */
    public Cart clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cartItemRepository.deleteByCart(cart);
        cart.clearCart();
        return cartRepository.save(cart);
    }
    
    /**
     * Get cart by user
     */
    public Optional<Cart> getCartByUser(User user) {
        return cartRepository.findByUserWithItems(user);
    }
    
    /**
     * Get cart items by cart
     */
    public List<CartItem> getCartItems(Cart cart) {
        return cartItemRepository.findByCartWithMenuItems(cart);
    }
    
    /**
     * Get cart item count for user
     */
    public Integer getCartItemCount(User user) {
        Optional<Cart> cart = cartRepository.findByUser(user);
        return cart.map(Cart::getTotalItems).orElse(0);
    }
    
    /**
     * Check if cart is empty
     */
    public boolean isCartEmpty(User user) {
        Optional<Cart> cart = cartRepository.findByUser(user);
        return cart.map(Cart::isEmpty).orElse(true);
    }
    
    /**
     * Get all carts (for admin purposes)
     */
    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }
    
    /**
     * Delete cart
     */
    public void deleteCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }
    
    /**
     * Get cart statistics
     */
    public CartStatistics getCartStatistics() {
        Long totalCarts = cartRepository.countTotalCarts();
        Long activeCarts = cartRepository.countActiveCarts();
        Long totalCartItems = cartItemRepository.countTotalCartItems();
        
        return new CartStatistics(totalCarts, activeCarts, totalCartItems);
    }
    
    /**
     * Inner class for cart statistics
     */
    public static class CartStatistics {
        private final Long totalCarts;
        private final Long activeCarts;
        private final Long totalCartItems;
        
        public CartStatistics(Long totalCarts, Long activeCarts, Long totalCartItems) {
            this.totalCarts = totalCarts;
            this.activeCarts = activeCarts;
            this.totalCartItems = totalCartItems;
        }
        
        public Long getTotalCarts() { return totalCarts; }
        public Long getActiveCarts() { return activeCarts; }
        public Long getTotalCartItems() { return totalCartItems; }
    }
}
