package com.example.ordermanagement.repository;

import com.example.ordermanagement.model.Cart;
import com.example.ordermanagement.model.CartItem;
import com.example.ordermanagement.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    /**
     * Find all cart items by cart
     */
    List<CartItem> findByCart(Cart cart);
    
    /**
     * Find cart item by cart and menu item
     */
    Optional<CartItem> findByCartAndMenuItem(Cart cart, MenuItem menuItem);
    
    /**
     * Find all cart items by cart ID
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId")
    List<CartItem> findByCartId(@Param("cartId") Long cartId);
    
    /**
     * Delete all cart items by cart
     */
    void deleteByCart(Cart cart);
    
    /**
     * Count cart items by cart
     */
    Long countByCart(Cart cart);
    
    /**
     * Find cart items with menu item details
     */
    @Query("SELECT ci FROM CartItem ci LEFT JOIN FETCH ci.menuItem WHERE ci.cart = :cart")
    List<CartItem> findByCartWithMenuItems(@Param("cart") Cart cart);
    
    /**
     * Check if menu item exists in any cart
     */
    boolean existsByMenuItem(MenuItem menuItem);
    
    /**
     * Count total cart items across all carts
     */
    @Query("SELECT COUNT(ci) FROM CartItem ci")
    Long countTotalCartItems();
}
