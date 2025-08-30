package com.example.ordermanagement.controller;

import com.example.ordermanagement.dto.ShopRequest;
import com.example.ordermanagement.dto.ShopResponse;
import com.example.ordermanagement.model.ShopStatus;
import com.example.ordermanagement.model.ShopType;
import com.example.ordermanagement.service.ShopServiceInterface;
import com.example.ordermanagement.service.MenuItemService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/shops")
public class ShopController {

    private static final Logger logger = LoggerFactory.getLogger(ShopController.class);

    @Autowired
    private ShopServiceInterface shopService;

    @Autowired
    private MenuItemService menuItemService;

    /**
     * Working shop list endpoint - bypasses template parsing issues
     */
    @GetMapping("/simple")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public String simpleShopList(Model model) {
        Sort sort = Sort.by("shopName").ascending();
        Pageable pageable = PageRequest.of(0, 20, sort);
        Page<ShopResponse> shops = shopService.getAllShops(pageable);
        
        logger.info("Simple endpoint - Retrieved {} shops", shops.getTotalElements());
        logger.debug("Simple endpoint - Content size: {}", shops.getContent().size());
        for (ShopResponse shop : shops.getContent()) {
            logger.debug("Shop: {} ({}) - Status: {}", shop.getShopName(), shop.getEmail(), shop.getStatus());
        }
        
        model.addAttribute("shops", shops);
        return "shops/list-simple";
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public String listShops(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "shopName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String city,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ShopResponse> shops;
        
        if (search != null && !search.trim().isEmpty()) {
            shops = shopService.searchShops(search.trim(), pageable);
        } else {
            shops = shopService.getAllShops(pageable);
        }

        // Debug logging
        logger.info("ShopController - Retrieved {} shops from service", shops.getTotalElements());
        logger.debug("ShopController - Page content size: {}", shops.getContent().size());
        for (ShopResponse shop : shops.getContent()) {
            logger.debug("Shop: {} ({})", shop.getShopName(), shop.getEmail());
        }

        model.addAttribute("shops", shops);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", shops.getTotalPages());
        model.addAttribute("totalElements", shops.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("search", search);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedCity", city);
        model.addAttribute("shopStatuses", ShopStatus.values());
        model.addAttribute("shopTypes", ShopType.values());

        // Temporarily use the working simple template until main template is fixed
        return "shops/list-simple";
    }

    /**
     * Show shop onboarding form
     */
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showShopForm(Model model) {
        model.addAttribute("shopRequest", new ShopRequest());
        model.addAttribute("shopTypes", ShopType.values());
        model.addAttribute("isEdit", false);
        return "shops/form";
    }

    /**
     * Process shop onboarding form submission
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String createShop(@Valid @ModelAttribute ShopRequest shopRequest,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("shopTypes", ShopType.values());
            model.addAttribute("isEdit", false);
            return "shops/form";
        }

        try {
            ShopResponse createdShop = shopService.createShop(shopRequest);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Shop '" + createdShop.getShopName() + "' has been successfully onboarded!");
            return "redirect:/shops/" + createdShop.getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error creating shop: " + e.getMessage());
            model.addAttribute("shopTypes", ShopType.values());
            model.addAttribute("isEdit", false);
            return "shops/form";
        }
    }

    /**
     * Display shop details with menu items
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public String viewShop(@PathVariable Long id, Model model) {
        try {
            ShopResponse shop = shopService.getShopById(id);
            model.addAttribute("shop", shop);
            
            // Get menu items for this shop
            var menuItems = menuItemService.getMenuItemsByShop(id);
            model.addAttribute("menuItems", menuItems);
            
            return "shops/view";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Shop not found: " + e.getMessage());
            return "redirect:/shops";
        }
    }

    /**
     * Show edit shop form
     */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            ShopResponse shop = shopService.getShopById(id);
            
            // Convert ShopResponse to ShopRequest for editing
            ShopRequest shopRequest = new ShopRequest();
            shopRequest.setShopName(shop.getShopName());
            shopRequest.setOwnerName(shop.getOwnerName());
            shopRequest.setEmail(shop.getEmail());
            shopRequest.setPhoneNumber(shop.getPhoneNumber());
            shopRequest.setAddress(shop.getAddress());
            shopRequest.setCity(shop.getCity());
            shopRequest.setState(shop.getState());
            shopRequest.setCountry(shop.getCountry());
            shopRequest.setPincode(shop.getPincode());
            shopRequest.setShopType(shop.getShopType());
            shopRequest.setDescription(shop.getDescription());
            
            model.addAttribute("shopRequest", shopRequest);
            model.addAttribute("shopTypes", ShopType.values());
            model.addAttribute("shop", shop);
            model.addAttribute("isEdit", true);
            
            return "shops/form";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Shop not found: " + e.getMessage());
            return "redirect:/shops";
        }
    }

    /**
     * Process shop update
     */
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateShop(@PathVariable Long id,
                           @Valid @ModelAttribute ShopRequest shopRequest,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        
        if (result.hasErrors()) {
            try {
                ShopResponse shop = shopService.getShopById(id);
                model.addAttribute("shop", shop);
                model.addAttribute("shopTypes", ShopType.values());
                model.addAttribute("isEdit", true);
                return "shops/form";
            } catch (Exception e) {
                return "redirect:/shops";
            }
        }

        try {
            ShopResponse updatedShop = shopService.updateShop(id, shopRequest);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Shop '" + updatedShop.getShopName() + "' has been successfully updated!");
            return "redirect:/shops/" + id;
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating shop: " + e.getMessage());
            try {
                ShopResponse shop = shopService.getShopById(id);
                model.addAttribute("shop", shop);
                model.addAttribute("shopTypes", ShopType.values());
                model.addAttribute("isEdit", true);
                return "shops/form";
            } catch (Exception ex) {
                return "redirect:/shops";
            }
        }
    }

    /**
     * Update shop status (activate, suspend, etc.)
     */
    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateShopStatus(@PathVariable Long id,
                                 @RequestParam ShopStatus status,
                                 RedirectAttributes redirectAttributes) {
        try {
            ShopResponse updatedShop = shopService.updateShopStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Shop status updated to: " + status.getDisplayName());
            return "redirect:/shops/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error updating shop status: " + e.getMessage());
            return "redirect:/shops/" + id;
        }
    }

    /**
     * Delete shop (soft delete)
     */
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteShop(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            shopService.deleteShop(id);
            redirectAttributes.addFlashAttribute("successMessage", "Shop has been successfully deleted!");
            return "redirect:/shops";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error deleting shop: " + e.getMessage());
            return "redirect:/shops/" + id;
        }
    }

    /**
     * Shop dashboard for shop owners
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('SHOP')")
    public String shopDashboard(Model model) {
        // This would be implemented to show shop-specific dashboard
        // For now, redirect to shop list
        return "redirect:/shops";
    }

    /**
     * Search shops by location
     */
    @GetMapping("/location/{city}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public String shopsByLocation(@PathVariable String city,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("shopName").ascending());
        Page<ShopResponse> shops = shopService.getShopsByCity(city, pageable);
        
        model.addAttribute("shops", shops);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", shops.getTotalPages());
        model.addAttribute("selectedCity", city);
        model.addAttribute("shopStatuses", ShopStatus.values());
        model.addAttribute("shopTypes", ShopType.values());
        
        return "shops/list";
    }

    /**
     * Get shops by type
     */
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOP')")
    public String shopsByType(@PathVariable ShopType type,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("shopName").ascending());
        Page<ShopResponse> shops = shopService.getShopsByType(type, pageable);
        
        model.addAttribute("shops", shops);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", shops.getTotalPages());
        model.addAttribute("selectedType", type);
        model.addAttribute("shopStatuses", ShopStatus.values());
        model.addAttribute("shopTypes", ShopType.values());
        
        return "shops/list";
    }

    /**
     * Debug endpoint to check shop data
     */
    @GetMapping("/debug")
    @PreAuthorize("hasRole('ADMIN')")
    public String debugShops(Model model) {
        List<ShopResponse> allShops = shopService.getAllShops();
        logger.info("DEBUG - Total shops from getAllShops(): {}", allShops.size());
        for (ShopResponse shop : allShops) {
            logger.debug("Shop: {} ({})", shop.getShopName(), shop.getEmail());
        }
        
        Pageable pageable = PageRequest.of(0, 20, Sort.by("shopName").ascending());
        Page<ShopResponse> pagedShops = shopService.getAllShops(pageable);
        logger.info("DEBUG - Paged shops total elements: {}", pagedShops.getTotalElements());
        logger.debug("DEBUG - Paged shops content size: {}", pagedShops.getContent().size());
        
        model.addAttribute("shops", pagedShops);
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", pagedShops.getTotalPages());
        model.addAttribute("totalElements", pagedShops.getTotalElements());
        model.addAttribute("sortBy", "shopName");
        model.addAttribute("sortDir", "asc");
        model.addAttribute("reverseSortDir", "desc");
        model.addAttribute("shopStatuses", ShopStatus.values());
        model.addAttribute("shopTypes", ShopType.values());
        
        return "shops/list";
    }
    
}
