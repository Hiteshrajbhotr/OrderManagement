package com.example.ordermanagement.controller;

import com.example.ordermanagement.model.Permission;
import com.example.ordermanagement.model.User;
import com.example.ordermanagement.model.UserPermission;
import com.example.ordermanagement.service.PermissionService;
import com.example.ordermanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/permissions")
public class PermissionController {

    private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    /**
     * List all permissions
     */
    @GetMapping
    @PreAuthorize("hasPermission(null, 'permissions:manage')")
    public String listPermissions(Model model) {
        List<Permission> permissions = permissionService.getAllPermissions();
        model.addAttribute("permissions", permissions);
        return "admin/permissions/list";
    }

    /**
     * Show create permission form
     */
    @GetMapping("/new")
    @PreAuthorize("hasPermission(null, 'permissions:manage')")
    public String showCreateForm(Model model) {
        model.addAttribute("permission", new Permission());
        model.addAttribute("isEdit", false);
        return "admin/permissions/form";
    }

    /**
     * Create new permission
     */
    @PostMapping
    @PreAuthorize("hasPermission(null, 'permissions:manage')")
    public String createPermission(@ModelAttribute Permission permission,
                                 RedirectAttributes redirectAttributes) {
        try {
            Permission createdPermission = permissionService.createPermission(
                permission.getName(),
                permission.getDescription(),
                permission.getResource(),
                permission.getAction()
            );
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Permission '" + createdPermission.getName() + "' created successfully!");
            return "redirect:/admin/permissions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error creating permission: " + e.getMessage());
            return "redirect:/admin/permissions/new";
        }
    }

    /**
     * Show edit permission form
     */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasPermission(null, 'permissions:manage')")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Permission> permission = permissionService.getPermissionById(id);
        if (permission.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Permission not found");
            return "redirect:/admin/permissions";
        }

        model.addAttribute("permission", permission.get());
        model.addAttribute("isEdit", true);
        return "admin/permissions/form";
    }

    /**
     * Update permission
     */
    @PostMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'permissions:manage')")
    public String updatePermission(@PathVariable Long id,
                                 @ModelAttribute Permission permission,
                                 RedirectAttributes redirectAttributes) {
        try {
            Permission updatedPermission = permissionService.updatePermission(
                id,
                permission.getName(),
                permission.getDescription(),
                permission.getResource(),
                permission.getAction()
            );
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Permission '" + updatedPermission.getName() + "' updated successfully!");
            return "redirect:/admin/permissions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error updating permission: " + e.getMessage());
            return "redirect:/admin/permissions/" + id + "/edit";
        }
    }

    /**
     * Deactivate permission
     */
    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasPermission(null, 'permissions:manage')")
    public String deactivatePermission(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            permissionService.deactivatePermission(id);
            redirectAttributes.addFlashAttribute("successMessage", "Permission deactivated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error deactivating permission: " + e.getMessage());
        }
        return "redirect:/admin/permissions";
    }

    /**
     * User permission management page
     */
    @GetMapping("/users")
    @PreAuthorize("hasPermission(null, 'permissions:manage')")
    public String manageUserPermissions(@RequestParam(required = false) Long userId, Model model) {
        List<User> users = userService.getAllUsers();
        List<Permission> permissions = permissionService.getActivePermissions();
        
        model.addAttribute("users", users);
        model.addAttribute("permissions", permissions);
        
        if (userId != null) {
            try {
                User selectedUser = userService.getUserById(userId);
                List<UserPermission> userPermissions = permissionService.getUserPermissionDetails(userId);
                model.addAttribute("selectedUser", selectedUser);
                model.addAttribute("userPermissions", userPermissions);
            } catch (RuntimeException e) {
                // User not found, ignore
            }
        }
        
        return "admin/permissions/user-permissions";
    }

    /**
     * Grant permission to user
     */
    @PostMapping("/users/{userId}/grant")
    @PreAuthorize("hasPermission(null, 'permissions:manage')")
    public String grantPermission(@PathVariable Long userId,
                                @RequestParam Long permissionId,
                                @RequestParam(required = false) String expiresAt,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            LocalDateTime expiration = null;
            
            if (expiresAt != null && !expiresAt.trim().isEmpty()) {
                expiration = LocalDateTime.parse(expiresAt);
            }
            
            UserPermission userPermission = permissionService.grantPermissionToUser(
                userId, permissionId, currentUser.getId(), expiration);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Permission granted successfully to user!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error granting permission: " + e.getMessage());
        }
        
        return "redirect:/admin/permissions/users?userId=" + userId;
    }

    /**
     * Revoke permission from user
     */
    @PostMapping("/users/{userId}/revoke")
    @PreAuthorize("hasPermission(null, 'permissions:manage')")
    public String revokePermission(@PathVariable Long userId,
                                 @RequestParam Long permissionId,
                                 @RequestParam(required = false) String reason,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            permissionService.revokePermissionFromUser(
                userId, permissionId, currentUser.getId(), reason);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Permission revoked successfully from user!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error revoking permission: " + e.getMessage());
        }
        
        return "redirect:/admin/permissions/users?userId=" + userId;
    }

    /**
     * Bulk grant permissions
     */
    @PostMapping("/users/{userId}/bulk-grant")
    @PreAuthorize("hasPermission(null, 'permissions:manage')")
    public String bulkGrantPermissions(@PathVariable Long userId,
                                     @RequestParam List<Long> permissionIds,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            permissionService.grantMultiplePermissions(userId, permissionIds, currentUser.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Permissions granted successfully to user!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error granting permissions: " + e.getMessage());
        }
        
        return "redirect:/admin/permissions/users?userId=" + userId;
    }

    /**
     * Bulk revoke permissions
     */
    @PostMapping("/users/{userId}/bulk-revoke")
    @PreAuthorize("hasPermission(null, 'permissions:manage')")
    public String bulkRevokePermissions(@PathVariable Long userId,
                                      @RequestParam List<Long> permissionIds,
                                      @RequestParam(required = false) String reason,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            permissionService.revokeMultiplePermissions(userId, permissionIds, currentUser.getId(), reason);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Permissions revoked successfully from user!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error revoking permissions: " + e.getMessage());
        }
        
        return "redirect:/admin/permissions/users?userId=" + userId;
    }

    /**
     * Search permissions
     */
    @GetMapping("/search")
    @PreAuthorize("hasPermission(null, 'permissions:manage')")
    public String searchPermissions(@RequestParam String query, Model model) {
        List<Permission> permissions = permissionService.searchPermissions(query);
        model.addAttribute("permissions", permissions);
        model.addAttribute("searchQuery", query);
        return "admin/permissions/list";
    }
}
