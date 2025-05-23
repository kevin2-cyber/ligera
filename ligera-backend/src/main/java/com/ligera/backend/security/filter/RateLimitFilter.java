package com.ligera.backend.security.filter;

import com.ligera.backend.service.RateLimitService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter for API rate limiting
 */
@Slf4j
@Component
@Order(1) // Make sure this runs early in the filter chain
@RequiredArgsConstructor
public class RateLimitFilter implements Filter {

    private final RateLimitService rateLimitService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // Skip rate limiting for static resources and non-API requests
        if (isStaticResource(httpRequest) || !isApiRequest(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Get the client IP address
        String ipAddress = getClientIp(httpRequest);
        
        // Get the user ID if authenticated
        String userId = getCurrentUserId();
        
        // Apply rate limiting
        rateLimitService.consumeRateLimit(userId, ipAddress);
        
        // Continue with the request
        chain.doFilter(request, response);
    }
    
    /**
     * Get the current authenticated user ID, or null if not authenticated
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
                !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return null;
    }
    
    /**
     * Check if the request is for a static resource
     */
    private boolean isStaticResource(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.contains("/static/") || 
               path.endsWith(".css") || 
               path.endsWith(".js") || 
               path.endsWith(".png") || 
               path.endsWith(".jpg") || 
               path.endsWith(".gif") || 
               path.endsWith(".ico") || 
               path.contains("/swagger-ui/") || 
               path.contains("/v3/api-docs/");
    }
    
    /**
     * Check if the request is an API request
     */
    private boolean isApiRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/");
    }
    
    /**
     * Get the client IP address, handling proxies
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Get the first IP in case of multiple proxies
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

