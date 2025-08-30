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

    @GetMapping("/shop/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'SHOP')")
    public String shopDashboard(Model model, HttpSession session) {
        // Get shop statistics using ShopService
        List<ShopResponse> allShops = shopService.getAllShops();
        
        model.addAttribute("totalShops", allShops.size());
        model.addAttribute("activeShops", allShops.stream()
            .filter(shop -> "ACTIVE".equals(shop.getStatus().toString()))
            .count());
        
        // Get total customers for reference
        List<User> allCustomers = userService.getUsersByRole(Role.CUSTOMER);
        model.addAttribute("totalCustomers", allCustomers.size());
        
        // Recent shops (last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long recentShops = allShops.stream()
            .filter(shop -> shop.getRegistrationDate() != null && 
                              shop.getRegistrationDate().isAfter(thirtyDaysAgo))
            .count();
        model.addAttribute("recentShops", recentShops);
        
        // Get recent shops (last 5)
        List<ShopResponse> recentShopsList = allShops.stream()
            .sorted((s1, s2) -> s2.getCreatedAt().compareTo(s1.getCreatedAt()))
            .limit(5)
            .collect(Collectors.toList());
        model.addAttribute("recentShopsList", recentShopsList);
        
        return "shop/dashboard";
    }

    @GetMapping("/customer/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'SHOP', 'CUSTOMER')")
    public String customerDashboard(Model model, HttpSession session) {
        // Get current user from session or authentication
        User currentUser = getCurrentUser(session);
        model.addAttribute("currentUser", currentUser);
        
        // Calculate days since joining
        if (currentUser != null && currentUser.getCreatedAt() != null) {
            long daysSinceJoined = ChronoUnit.DAYS.between(
                currentUser.getCreatedAt().toLocalDate(), 
                LocalDate.now()
            );
            model.addAttribute("daysSinceJoined", daysSinceJoined);
        } else {
            model.addAttribute("daysSinceJoined", 0);
        }
        
        // Get customer statistics and data
        List<com.example.ordermanagement.dto.CustomerResponse> allCustomers = customerService.getAllCustomers();
        model.addAttribute("totalCustomers", allCustomers.size());
        model.addAttribute("activeCustomers", allCustomers.size()); // All customers are considered active
        
        // Get unique cities
        List<String> cities = allCustomers.stream()
            .map(com.example.ordermanagement.dto.CustomerResponse::getCity)
            .filter(city -> city != null && !city.trim().isEmpty())
            .distinct()
            .collect(Collectors.toList());
        model.addAttribute("totalLocations", cities.size());
        
        // Recent registrations (customers registered in the last 30 days)
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        long recentRegistrations = allCustomers.stream()
            .filter(customer -> customer.getRegistrationDate() != null && 
                              customer.getRegistrationDate().isAfter(thirtyDaysAgo))
            .count();
        model.addAttribute("recentRegistrations", recentRegistrations);
        
        // Get recent customers (last 5)
        List<com.example.ordermanagement.dto.CustomerResponse> recentCustomers = allCustomers.stream()
            .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
            .limit(5)
            .collect(Collectors.toList());
        model.addAttribute("recentCustomers", recentCustomers);
        
        return "customer/dashboard";
    }

    @GetMapping("/dashboard")
    public String defaultDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            // Redirect to appropriate dashboard based on role
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/admin/dashboard";
            } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SHOP"))) {
                return "redirect:/shop/dashboard";
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
