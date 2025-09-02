package com.example.ordermanagement.repository;

import com.example.ordermanagement.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    Optional<Permission> findByName(String name);
    
    List<Permission> findByIsActiveTrue();
    
    List<Permission> findByResource(String resource);
    
    List<Permission> findByResourceAndAction(String resource, String action);
    
    @Query("SELECT p FROM Permission p WHERE p.resource = :resource AND p.isActive = true")
    List<Permission> findActivePermissionsByResource(@Param("resource") String resource);
    
    @Query("SELECT p FROM Permission p WHERE p.name LIKE %:search% OR p.description LIKE %:search%")
    List<Permission> searchPermissions(@Param("search") String search);
    
    boolean existsByName(String name);
    
    boolean existsByResourceAndAction(String resource, String action);
}
