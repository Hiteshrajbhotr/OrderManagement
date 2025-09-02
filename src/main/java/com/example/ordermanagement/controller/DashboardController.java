package com.example.ordermanagement.controller;

import com.example.ordermanagement.model.Role;
import com.example.ordermanagement.model.User;
import com.example.ordermanagement.service.CustomerServiceInterface;
import com.example.ordermanagement.service.UserService;
import com.example.ordermanagement.service.ShopServiceInterface;
import com.example.ordermanagement.dto.ShopResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final UserService userService;
    private final CustomerServiceInterface customerService;
    private final ShopServiceInterface shopService;

    public DashboardController(UserService userService, CustomerServiceInterface customerService, ShopServiceInterface shopService) {
        this.userService = userService;
        this.customerService = customerService;
        this.shopService = shopService;
    }

    // Shop dashboard moved to ShopController at /shops/dashboard

    // Customer dashboard moved to CustomerDashboardController

    @GetMapping("/dashboard")
    public String defaultDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            // Redirect to appropriate dashboard based on role
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/admin/dashboard";
            } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SHOP"))) {
                return "redirect:/shops/dashboard";
            } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"))) {
                return "redirect:/customer/dashboard";
            }
        }
        return "redirect:/login";
    }

    private User getCurrentUser(HttpSession session) {
        // Try to get user from session first
        User sessionUser = (User) session.getAttribute("currentUser");
        if (sessionUser != null) {
            return sessionUser;
        }
        
        // Fallback to getting user from authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            Optional<User> userOpt = userService.getUserByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                session.setAttribute("currentUser", user); // Cache in session
                return user;
            }
        }
        
        return null;
    }
}
