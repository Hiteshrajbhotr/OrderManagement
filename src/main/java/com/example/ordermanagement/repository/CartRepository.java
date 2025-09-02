package com.example.ordermanagement.repository;

import com.example.ordermanagement.model.Cart;
import com.example.ordermanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    /**
     * Find cart by user
     */
    Optional<Cart> findByUser(User user);
    
    /**
     * Find cart by user ID
     */
    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId")
    Optional<Cart> findByUserId(@Param("userId") Long userId);
    
    /**
     * Check if user has a cart
     */
    boolean existsByUser(User user);
    
    /**
     * Delete cart by user
     */
    void deleteByUser(User user);
    
    /**
     * Find cart with cart items by user
     */
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems ci LEFT JOIN FETCH ci.menuItem WHERE c.user = :user")
    Optional<Cart> findByUserWithItems(@Param("user") User user);
    
    /**
     * Count total carts
     */
    @Query("SELECT COUNT(c) FROM Cart c")
    Long countTotalCarts();
    
    /**
     * Count active carts (carts with items)
     */
    @Query("SELECT COUNT(c) FROM Cart c WHERE c.totalItems > 0")
    Long countActiveCarts();
}
