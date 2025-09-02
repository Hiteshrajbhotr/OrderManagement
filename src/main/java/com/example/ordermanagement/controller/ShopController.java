package com.example.ordermanagement.controller;

import com.example.ordermanagement.dto.MenuItemRequest;
import com.example.ordermanagement.dto.ShopRequest;
import com.example.ordermanagement.dto.ShopResponse;
import com.example.ordermanagement.model.ShopStatus;
import com.example.ordermanagement.model.ShopType;
import com.example.ordermanagement.model.User;
import com.example.ordermanagement.service.FileUploadService;
import com.example.ordermanagement.service.ShopServiceInterface;
import com.example.ordermanagement.service.MenuItemService;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/shops")
public class ShopController {

    private static final Logger logger = LoggerFactory.getLogger(ShopController.class);

    @Autowired
    private ShopServiceInterface shopService;

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * Working shop list endpoint - bypasses template parsing issues
     */
    @GetMapping("/simple")
    @PreAuthorize("hasPermission(null, 'shops:view')")
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
    @PreAuthorize("hasPermission(null, 'shops:view')")
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
        } else if (status != null && !status.trim().isEmpty()) {
            try {
                ShopStatus shopStatus = ShopStatus.valueOf(status.toUpperCase());
                shops = shopService.getShopsByStatus(shopStatus, pageable);
            } catch (IllegalArgumentException e) {
                shops = shopService.getAllShops(pageable);
            }
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
    @PreAuthorize("hasPermission(null, 'shops:create')")
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
    @PreAuthorize("hasPermission(null, 'shops:create')")
    public String createShop(@Valid @ModelAttribute ShopRequest shopRequest,
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("shopTypes", ShopType.values());
            model.addAttribute("isEdit", false);
            return "shops/form";
        }

        try {
            // Handle image upload if provided
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = fileUploadService.uploadFile(imageFile, "shop-images");
                shopRequest.setImageUrl(imageUrl);
            }
            
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
    @PreAuthorize("hasPermission(null, 'shops:view')")
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
    @PreAuthorize("hasPermission(null, 'shops:edit')")
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
            shopRequest.setImageUrl(shop.getImageUrl());
            
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
    @PreAuthorize("hasPermission(null, 'shops:edit')")
    public String updateShop(@PathVariable Long id,
                           @Valid @ModelAttribute ShopRequest shopRequest,
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
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
            // Handle image upload if provided
            if (imageFile != null && !imageFile.isEmpty()) {
                // Get existing shop to check for old image
                ShopResponse existingShop = shopService.getShopById(id);
                if (existingShop.getImageUrl() != null) {
                    // Delete old image
                    fileUploadService.deleteFile(existingShop.getImageUrl());
                }
                
                String imageUrl = fileUploadService.uploadFile(imageFile, "shop-images");
                shopRequest.setImageUrl(imageUrl);
            }
            
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
    @PreAuthorize("hasPermission(null, 'shops:manage')")
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
    @PreAuthorize("hasPermission(null, 'shops:delete')")
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
     * Shop dashboard for shop owners - shows only current user's shop
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasPermission(null, 'dashboard:shop')")
    public String shopDashboard(Model model, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            
            // Check if current user owns a shop
            if (!shopService.userOwnsShop(currentUser.getId())) {
                model.addAttribute("error", "You don't have a shop associated with your account. Please contact admin.");
                model.addAttribute("menuItems", new ArrayList<>());
                model.addAttribute("totalMenuItems", 0);
                model.addAttribute("availableItems", 0);
                model.addAttribute("totalOrders", 0);
                model.addAttribute("totalRevenue", "$0.00");
                return "shop/dashboard";
            }
            
            // Get current user's shop
            ShopResponse shop = shopService.getShopByOwnerUserId(currentUser.getId());
            model.addAttribute("shop", shop);
            
            // Get menu items for this shop
            var menuItems = menuItemService.getMenuItemsByShop(shop.getId());
            model.addAttribute("menuItems", menuItems != null ? menuItems : new ArrayList<>());
            
            // Add statistics
            int totalItems = menuItems != null ? menuItems.size() : 0;
            int availableItems = menuItems != null ? 
                (int) menuItems.stream().filter(item -> item.getIsAvailable()).count() : 0;
                
            model.addAttribute("totalMenuItems", totalItems);
            model.addAttribute("availableItems", availableItems);
            model.addAttribute("totalOrders", 0); // Placeholder
            model.addAttribute("totalRevenue", "$0.00"); // Placeholder
            
            return "shop/dashboard";
        } catch (Exception e) {
            logger.error("Error loading shop dashboard for user: {}", authentication.getName(), e);
            model.addAttribute("error", "Error loading shop dashboard: " + e.getMessage());
            model.addAttribute("menuItems", new ArrayList<>());
            model.addAttribute("totalMenuItems", 0);
            model.addAttribute("availableItems", 0);
            model.addAttribute("totalOrders", 0);
            model.addAttribute("totalRevenue", "$0.00");
            return "shop/dashboard";
        }
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

    /**
     * Add new menu item - only for current user's shop
     */
    @PostMapping("/menu-items")
    @PreAuthorize("hasPermission(null, 'menu-items:create')")
    public String addMenuItem(@Valid @ModelAttribute MenuItemRequest menuItemRequest,
                             BindingResult result,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                             RedirectAttributes redirectAttributes,
                             Authentication authentication) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please check the form for errors.");
            return "redirect:/shops/dashboard";
        }

        try {
            User currentUser = (User) authentication.getPrincipal();
            
            // Check if current user owns a shop
            if (!shopService.userOwnsShop(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You don't have a shop associated with your account.");
                return "redirect:/shops/dashboard";
            }
            
            // Get current user's shop
            ShopResponse shop = shopService.getShopByOwnerUserId(currentUser.getId());
            
            // Handle image upload if provided
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String imageUrl = fileUploadService.uploadFile(imageFile, "menu-items");
                    menuItemRequest.setImageUrl(imageUrl);
                } catch (Exception e) {
                    logger.error("Error uploading image", e);
                    redirectAttributes.addFlashAttribute("errorMessage", "Error uploading image: " + e.getMessage());
                    return "redirect:/shops/dashboard";
                }
            }
            
            // Set the shop ID to current user's shop
            menuItemRequest.setShopId(shop.getId());
            menuItemService.createMenuItem(menuItemRequest);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Menu item '" + menuItemRequest.getItemName() + "' has been added successfully!");
            
        } catch (Exception e) {
            logger.error("Error adding menu item for user: {}", authentication.getName(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding menu item: " + e.getMessage());
        }
        
        return "redirect:/shops/dashboard";
    }

    /**
     * Toggle menu item availability - only for current user's shop
     */
    @PostMapping("/menu-items/{id}/toggle-availability")
    @PreAuthorize("hasPermission(null, 'menu-items:edit')")
    @ResponseBody
    public ResponseEntity<String> toggleMenuItemAvailability(@PathVariable Long id, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            
            // Check if current user owns a shop
            if (!shopService.userOwnsShop(currentUser.getId())) {
                return ResponseEntity.badRequest().body("You don't have a shop associated with your account");
            }
            
            // Verify the menu item belongs to current user's shop
            var menuItem = menuItemService.getMenuItemById(id);
            ShopResponse userShop = shopService.getShopByOwnerUserId(currentUser.getId());
            
            if (!menuItem.getShopId().equals(userShop.getId())) {
                return ResponseEntity.badRequest().body("You can only modify menu items from your own shop");
            }
            
            menuItemService.toggleAvailability(id);
            return ResponseEntity.ok("Availability updated successfully");
        } catch (Exception e) {
            logger.error("Error toggling menu item availability for user: {}", authentication.getName(), e);
            return ResponseEntity.badRequest().body("Error updating availability");
        }
    }

    /**
     * Delete menu item - only for current user's shop
     */
    @PostMapping("/menu-items/{id}/delete")
    @PreAuthorize("hasPermission(null, 'menu-items:delete')")
    @ResponseBody
    public ResponseEntity<String> deleteMenuItem(@PathVariable Long id, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            
            // Check if current user owns a shop
            if (!shopService.userOwnsShop(currentUser.getId())) {
                return ResponseEntity.badRequest().body("You don't have a shop associated with your account");
            }
            
            // Verify the menu item belongs to current user's shop
            var menuItem = menuItemService.getMenuItemById(id);
            ShopResponse userShop = shopService.getShopByOwnerUserId(currentUser.getId());
            
            if (!menuItem.getShopId().equals(userShop.getId())) {
                return ResponseEntity.badRequest().body("You can only delete menu items from your own shop");
            }
            
            menuItemService.deleteMenuItem(id);
            return ResponseEntity.ok("Menu item deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting menu item for user: {}", authentication.getName(), e);
            return ResponseEntity.badRequest().body("Error deleting menu item");
        }
    }

    /**
     * Show edit menu item form - only for current user's shop
     */
    @GetMapping("/menu-items/{id}/edit")
    @PreAuthorize("hasPermission(null, 'menu-items:edit')")
    public String showEditMenuItemForm(@PathVariable Long id, Model model, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            
            // Check if current user owns a shop
            if (!shopService.userOwnsShop(currentUser.getId())) {
                model.addAttribute("error", "You don't have a shop associated with your account.");
                return "redirect:/shops/dashboard";
            }
            
            // Verify the menu item belongs to current user's shop
            var menuItem = menuItemService.getMenuItemById(id);
            ShopResponse userShop = shopService.getShopByOwnerUserId(currentUser.getId());
            
            if (!menuItem.getShopId().equals(userShop.getId())) {
                model.addAttribute("error", "You can only edit menu items from your own shop.");
                return "redirect:/shops/dashboard";
            }
            
            model.addAttribute("menuItem", menuItem);
            model.addAttribute("isEdit", true);
            return "shop/menu-item-form";
        } catch (Exception e) {
            logger.error("Error loading menu item for edit for user: {}", authentication.getName(), e);
            return "redirect:/shops/dashboard";
        }
    }

    /**
     * Update menu item - only for current user's shop
     */
    @PostMapping("/menu-items/{id}")
    @PreAuthorize("hasPermission(null, 'menu-items:edit')")
    public String updateMenuItem(@PathVariable Long id,
                                @Valid @ModelAttribute MenuItemRequest menuItemRequest,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Authentication authentication) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please check the form for errors.");
            return "redirect:/shops/menu-items/" + id + "/edit";
        }

        try {
            User currentUser = (User) authentication.getPrincipal();
            
            // Check if current user owns a shop
            if (!shopService.userOwnsShop(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You don't have a shop associated with your account.");
                return "redirect:/shops/dashboard";
            }
            
            // Verify the menu item belongs to current user's shop
            var existingMenuItem = menuItemService.getMenuItemById(id);
            ShopResponse userShop = shopService.getShopByOwnerUserId(currentUser.getId());
            
            if (!existingMenuItem.getShopId().equals(userShop.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You can only update menu items from your own shop.");
                return "redirect:/shops/dashboard";
            }
            
            // Ensure the shop ID is set correctly
            menuItemRequest.setShopId(userShop.getId());
            
            menuItemService.updateMenuItem(id, menuItemRequest);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Menu item '" + menuItemRequest.getItemName() + "' has been updated successfully!");
            
        } catch (Exception e) {
            logger.error("Error updating menu item for user: {}", authentication.getName(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating menu item: " + e.getMessage());
        }
        
        return "redirect:/shops/dashboard";
    }
    
}
