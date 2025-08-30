package com.example.ordermanagement.controller;

import com.example.ordermanagement.model.Role;
import com.example.ordermanagement.model.User;
import com.example.ordermanagement.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // Admin dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        UserService.UserStats stats = userService.getUserStats();
        model.addAttribute("stats", stats);
        
        // Get recent users (last 5)
        List<User> recentUsers = userService.getAllUsers()
            .stream()
            .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
            .limit(5)
            .toList();
        model.addAttribute("recentUsers", recentUsers);
        
        return "admin/dashboard";
    }

    // User management - list all users
    @GetMapping("/users")
    public String listUsers(Model model, @RequestParam(required = false) String search) {
        List<User> users;
        
        if (search != null && !search.trim().isEmpty()) {
            users = userService.searchUsers(search.trim());
            model.addAttribute("searchQuery", search);
        } else {
            users = userService.getAllUsers();
        }
        
        model.addAttribute("users", users);
        return "admin/users";
    }

    // View user details
    @GetMapping("/users/{id}")
    public String viewUser(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id);
            model.addAttribute("user", user);
            return "admin/user-details";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
            return "redirect:/admin/users";
        }
    }

    // Toggle user status (activate/deactivate)
    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.toggleUserStatus(id);
            String status = user.getIsActive() ? "activated" : "deactivated";
            redirectAttributes.addFlashAttribute("successMessage", 
                "User " + user.getUsername() + " has been " + status + " successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating user status: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    // Update user role
    @PostMapping("/users/{id}/update-role")
    public String updateUserRole(@PathVariable Long id, 
                                @RequestParam Role role, 
                                RedirectAttributes redirectAttributes) {
        try {
            User user = userService.updateUserRole(id, role);
            redirectAttributes.addFlashAttribute("successMessage", 
                "User " + user.getUsername() + " role updated to " + role.name() + " successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating user role: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    // Delete user
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id);
            String username = user.getUsername();
            
            // Prevent admin from deleting themselves
            // In a real application, you'd get the current user from SecurityContext
            if ("admin".equals(username)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete the main admin user!");
                return "redirect:/admin/users";
            }
            
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "User " + username + " has been deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting user: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    // Get users by role (AJAX endpoint)
    @GetMapping("/users/role/{role}")
    @ResponseBody
    public List<User> getUsersByRole(@PathVariable Role role) {
        return userService.getUsersByRole(role);
    }

    // Get user statistics (AJAX endpoint)
    @GetMapping("/stats")
    @ResponseBody
    public UserService.UserStats getUserStats() {
        return userService.getUserStats();
    }
}
