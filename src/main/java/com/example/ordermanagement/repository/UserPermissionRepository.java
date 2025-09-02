package com.example.ordermanagement.repository;

import com.example.ordermanagement.model.User;
import com.example.ordermanagement.model.Permission;
import com.example.ordermanagement.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    
    List<UserPermission> findByUser(User user);
    
    List<UserPermission> findByUserAndIsActiveTrue(User user);
    
    List<UserPermission> findByPermission(Permission permission);
    
    Optional<UserPermission> findByUserAndPermission(User user, Permission permission);
    
    @Query("SELECT up FROM UserPermission up WHERE up.user = :user AND up.permission = :permission AND up.isActive = true")
    Optional<UserPermission> findActiveUserPermission(@Param("user") User user, @Param("permission") Permission permission);
    
    @Query("SELECT up FROM UserPermission up WHERE up.user = :user AND up.isActive = true AND (up.expiresAt IS NULL OR up.expiresAt > :now)")
    List<UserPermission> findEffectiveUserPermissions(@Param("user") User user, @Param("now") LocalDateTime now);
    
    @Query("SELECT up FROM UserPermission up WHERE up.user.id = :userId AND up.permission.resource = :resource AND up.permission.action = :action AND up.isActive = true AND (up.expiresAt IS NULL OR up.expiresAt > :now)")
    Optional<UserPermission> findEffectiveUserPermissionByResourceAndAction(
            @Param("userId") Long userId, 
            @Param("resource") String resource, 
            @Param("action") String action, 
            @Param("now") LocalDateTime now);
    
    @Query("SELECT up FROM UserPermission up WHERE up.grantedBy = :grantedBy")
    List<UserPermission> findByGrantedBy(@Param("grantedBy") Long grantedBy);
    
    @Query("SELECT up FROM UserPermission up WHERE up.expiresAt IS NOT NULL AND up.expiresAt <= :now AND up.isActive = true")
    List<UserPermission> findExpiredPermissions(@Param("now") LocalDateTime now);
    
    boolean existsByUserAndPermissionAndIsActiveTrue(User user, Permission permission);
}
