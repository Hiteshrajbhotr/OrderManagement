package com.example.ordermanagement.security;

import com.example.ordermanagement.model.User;
import com.example.ordermanagement.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class DynamicPermissionEvaluator implements PermissionEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(DynamicPermissionEvaluator.class);

    @Autowired
    private PermissionService permissionService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || permission == null) {
            return false;
        }

        User user = (User) authentication.getPrincipal();
        String permissionString = permission.toString();

        // Handle different permission formats
        if (permissionString.contains(":")) {
            // Format: "resource:action"
            String[] parts = permissionString.split(":");
            if (parts.length == 2) {
                return permissionService.hasPermission(user.getId(), parts[0], parts[1]);
            }
        } else {
            // Handle permission name directly
            return permissionService.hasPermissionByName(user.getId(), permissionString);
        }

        logger.debug("Permission check failed for user {} with permission {}", user.getUsername(), permissionString);
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || permission == null) {
            return false;
        }

        User user = (User) authentication.getPrincipal();
        String permissionString = permission.toString();

        // For resource-specific permissions (e.g., shop:123:edit)
        if (targetId != null && targetType != null) {
            String resource = targetType + ":" + targetId;
            return permissionService.hasPermission(user.getId(), resource, permissionString);
        }

        return hasPermission(authentication, null, permission);
    }

    // Helper method for common permission checks
    public boolean canAccessResource(Authentication authentication, String resource, String action) {
        if (authentication == null) {
            return false;
        }

        User user = (User) authentication.getPrincipal();
        return permissionService.hasPermission(user.getId(), resource, action);
    }

    // Convenience methods for common operations
    public boolean canViewShops(Authentication authentication) {
        return canAccessResource(authentication, "shops", "view");
    }

    public boolean canCreateShops(Authentication authentication) {
        return canAccessResource(authentication, "shops", "create");
    }

    public boolean canEditShops(Authentication authentication) {
        return canAccessResource(authentication, "shops", "edit");
    }

    public boolean canDeleteShops(Authentication authentication) {
        return canAccessResource(authentication, "shops", "delete");
    }

    public boolean canManageUsers(Authentication authentication) {
        return canAccessResource(authentication, "users", "manage");
    }

    public boolean canManagePermissions(Authentication authentication) {
        return canAccessResource(authentication, "permissions", "manage");
    }

    public boolean canViewDashboard(Authentication authentication, String dashboardType) {
        return canAccessResource(authentication, "dashboard", dashboardType);
    }
}
