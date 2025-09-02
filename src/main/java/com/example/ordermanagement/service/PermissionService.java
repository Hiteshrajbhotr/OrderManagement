package com.example.ordermanagement.service;

import com.example.ordermanagement.model.Permission;
import com.example.ordermanagement.model.User;
import com.example.ordermanagement.model.UserPermission;
import com.example.ordermanagement.repository.PermissionRepository;
import com.example.ordermanagement.repository.UserPermissionRepository;
import com.example.ordermanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class PermissionService {

    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserPermissionRepository userPermissionRepository;

    @Autowired
    private UserRepository userRepository;

    // Permission CRUD operations
    public Permission createPermission(String name, String description, String resource, String action) {
        if (permissionRepository.existsByName(name)) {
            throw new IllegalArgumentException("Permission with name '" + name + "' already exists");
        }

        if (permissionRepository.existsByResourceAndAction(resource, action)) {
            throw new IllegalArgumentException("Permission for resource '" + resource + "' and action '" + action + "' already exists");
        }

        Permission permission = new Permission(name, description, resource, action);
        Permission savedPermission = permissionRepository.save(permission);
        logger.info("Created new permission: {}", savedPermission);
        return savedPermission;
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public List<Permission> getActivePermissions() {
        return permissionRepository.findByIsActiveTrue();
    }

    public Optional<Permission> getPermissionById(Long id) {
        return permissionRepository.findById(id);
    }

    public Optional<Permission> getPermissionByName(String name) {
        return permissionRepository.findByName(name);
    }

    public List<Permission> getPermissionsByResource(String resource) {
        return permissionRepository.findActivePermissionsByResource(resource);
    }

    public Permission updatePermission(Long id, String name, String description, String resource, String action) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with id: " + id));

        // Check for name conflicts (excluding current permission)
        if (!permission.getName().equals(name) && permissionRepository.existsByName(name)) {
            throw new IllegalArgumentException("Permission with name '" + name + "' already exists");
        }

        permission.setName(name);
        permission.setDescription(description);
        permission.setResource(resource);
        permission.setAction(action);

        Permission updatedPermission = permissionRepository.save(permission);
        logger.info("Updated permission: {}", updatedPermission);
        return updatedPermission;
    }

    public void deactivatePermission(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with id: " + id));

        permission.setIsActive(false);
        permissionRepository.save(permission);
        logger.info("Deactivated permission: {}", permission.getName());
    }

    // User Permission Management
    public UserPermission grantPermissionToUser(Long userId, Long permissionId, Long grantedBy) {
        return grantPermissionToUser(userId, permissionId, grantedBy, null);
    }

    public UserPermission grantPermissionToUser(Long userId, Long permissionId, Long grantedBy, LocalDateTime expiresAt) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with id: " + permissionId));

        if (!permission.getIsActive()) {
            throw new IllegalArgumentException("Cannot grant inactive permission: " + permission.getName());
        }

        // Check if user already has this permission
        Optional<UserPermission> existingPermission = userPermissionRepository.findByUserAndPermission(user, permission);
        if (existingPermission.isPresent() && existingPermission.get().getIsActive()) {
            throw new IllegalArgumentException("User already has this permission: " + permission.getName());
        }

        UserPermission userPermission = new UserPermission(user, permission, grantedBy, expiresAt);
        UserPermission savedUserPermission = userPermissionRepository.save(userPermission);
        
        logger.info("Granted permission '{}' to user '{}' by user ID {}", 
                permission.getName(), user.getUsername(), grantedBy);
        
        return savedUserPermission;
    }

    public void revokePermissionFromUser(Long userId, Long permissionId, Long revokedBy, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with id: " + permissionId));

        UserPermission userPermission = userPermissionRepository.findActiveUserPermission(user, permission)
                .orElseThrow(() -> new IllegalArgumentException("User does not have this permission: " + permission.getName()));

        userPermission.revoke(revokedBy, reason);
        userPermissionRepository.save(userPermission);

        logger.info("Revoked permission '{}' from user '{}' by user ID {}. Reason: {}", 
                permission.getName(), user.getUsername(), revokedBy, reason);
    }

    // Permission Checking
    public boolean hasPermission(Long userId, String resource, String action) {
        LocalDateTime now = LocalDateTime.now();
        Optional<UserPermission> userPermission = userPermissionRepository
                .findEffectiveUserPermissionByResourceAndAction(userId, resource, action, now);
        
        return userPermission.isPresent();
    }

    public boolean hasPermission(User user, String resource, String action) {
        return hasPermission(user.getId(), resource, action);
    }

    public boolean hasPermissionByName(Long userId, String permissionName) {
        Optional<Permission> permission = permissionRepository.findByName(permissionName);
        if (permission.isEmpty()) {
            return false;
        }

        return hasPermission(userId, permission.get().getResource(), permission.get().getAction());
    }

    public Set<String> getUserPermissions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        LocalDateTime now = LocalDateTime.now();
        List<UserPermission> effectivePermissions = userPermissionRepository.findEffectiveUserPermissions(user, now);

        return effectivePermissions.stream()
                .map(up -> up.getPermission().getFullPermission())
                .collect(Collectors.toSet());
    }

    public List<UserPermission> getUserPermissionDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return userPermissionRepository.findByUserAndIsActiveTrue(user);
    }

    // Bulk operations
    public void grantMultiplePermissions(Long userId, List<Long> permissionIds, Long grantedBy) {
        for (Long permissionId : permissionIds) {
            try {
                grantPermissionToUser(userId, permissionId, grantedBy);
            } catch (IllegalArgumentException e) {
                logger.warn("Failed to grant permission {} to user {}: {}", permissionId, userId, e.getMessage());
            }
        }
    }

    public void revokeMultiplePermissions(Long userId, List<Long> permissionIds, Long revokedBy, String reason) {
        for (Long permissionId : permissionIds) {
            try {
                revokePermissionFromUser(userId, permissionId, revokedBy, reason);
            } catch (IllegalArgumentException e) {
                logger.warn("Failed to revoke permission {} from user {}: {}", permissionId, userId, e.getMessage());
            }
        }
    }

    // Maintenance operations
    public void cleanupExpiredPermissions() {
        LocalDateTime now = LocalDateTime.now();
        List<UserPermission> expiredPermissions = userPermissionRepository.findExpiredPermissions(now);
        
        for (UserPermission userPermission : expiredPermissions) {
            userPermission.setIsActive(false);
            userPermissionRepository.save(userPermission);
        }
        
        if (!expiredPermissions.isEmpty()) {
            logger.info("Cleaned up {} expired permissions", expiredPermissions.size());
        }
    }

    public List<Permission> searchPermissions(String searchTerm) {
        return permissionRepository.searchPermissions(searchTerm);
    }
}
