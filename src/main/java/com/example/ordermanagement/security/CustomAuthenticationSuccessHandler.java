package com.example.ordermanagement.security;

import com.example.ordermanagement.model.User;
import com.example.ordermanagement.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    public CustomAuthenticationSuccessHandler(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> userOpt = userService.getUserByUsername(userDetails.getUsername());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Update last login
            userService.updateLastLogin(user.getUsername());
            
            // Store user in session
            request.getSession().setAttribute("currentUser", user);
            
            // Redirect based on role
            String redirectUrl;
            if (user.isAdmin()) {
                redirectUrl = "/admin/dashboard";
            } else if (user.isShop()) {
                redirectUrl = "/shops/dashboard";
            } else {
                redirectUrl = "/customer/dashboard";
            }
            
            response.sendRedirect(redirectUrl);
        } else {
            // Fallback redirect
            response.sendRedirect("/login?error=true");
        }
    }
}
