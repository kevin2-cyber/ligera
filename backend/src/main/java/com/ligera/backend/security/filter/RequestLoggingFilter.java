package com.ligera.backend.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Filter to log all HTTP requests and responses for security and debugging purposes
 */
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    private static final int MAX_PAYLOAD_LENGTH = 1000; // Limit payload logging to prevent excessive logs
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        // Skip logging for static resources
        if (isStaticResource(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Wrap request and response to allow reading the body multiple times
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Log pre-processing of request
            logRequest(requestWrapper);
            
            // Execute request
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            // Log post-processing of response
            long duration = System.currentTimeMillis() - startTime;
            logResponse(responseWrapper, duration);
            
            // Copy response back to client
            responseWrapper.copyBodyToResponse();
        }
    }
    
    /**
     * Log the incoming request
     */
    private void logRequest(ContentCachingRequestWrapper request) {
        String requestId = generateRequestId();
        request.setAttribute("requestId", requestId);
        
        if (log.isDebugEnabled()) {
            // Detailed debug logging
            log.debug("[REQUEST] [{}] [{}] [{}] [{}] [{}] [Headers: {}] [Parameters: {}]",
                    requestId,
                    LocalDateTime.now().format(TIMESTAMP_FORMAT),
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr(),
                    getHeadersAsMap(request),
                    getParametersAsMap(request)
            );
        } else {
            // Basic info logging
            log.info("[REQUEST] [{}] [{}] [{}] [{}] [{}]",
                    requestId,
                    LocalDateTime.now().format(TIMESTAMP_FORMAT),
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr()
            );
        }
    }
    
    /**
     * Log the outgoing response
     */
    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        HttpServletRequest request = 
                (HttpServletRequest) response.getRequest();
        String requestId = (String) request.getAttribute("requestId");
        
        if (log.isDebugEnabled()) {
            // Detailed debug logging
            byte[] responseBody = response.getContentAsByteArray();
            String responseBodyContent = new String(responseBody);
            if (responseBody.length > MAX_PAYLOAD_LENGTH) {
                responseBodyContent = responseBodyContent.substring(0, MAX_PAYLOAD_LENGTH) + "... [truncated]";
            }
            
            log.debug("[RESPONSE] [{}] [{}] [Status: {}] [Duration: {} ms] [Content-Type: {}] [Body: {}]",
                    requestId,
                    LocalDateTime.now().format(TIMESTAMP_FORMAT),
                    response.getStatus(),
                    duration,
                    response.getContentType(),
                    responseBodyContent
            );
        } else {
            // Basic info logging
            log.info("[RESPONSE] [{}] [{}] [Status: {}] [Duration: {} ms]",
                    requestId,
                    LocalDateTime.now().format(TIMESTAMP_FORMAT),
                    response.getStatus(),
                    duration
            );
        }
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
     * Generate a unique request ID
     */
    private String generateRequestId() {
        return "req-" + System.currentTimeMillis() + "-" + 
                Math.abs(System.nanoTime() % 10000);
    }
    
    /**
     * Extract request headers as a map
     */
    private Map<String, String> getHeadersAsMap(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            
            // Skip logging sensitive headers
            if (isSensitiveHeader(name)) {
                headers.put(name, "[REDACTED]");
            } else {
                headers.put(name, request.getHeader(name));
            }
        }
        
        return headers;
    }
    
    /**
     * Extract request parameters as a map
     */
    private Map<String, String> getParametersAsMap(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            
            // Skip logging sensitive parameters
            if (isSensitiveParameter(name)) {
                parameters.put(name, "[REDACTED]");
            } else {
                parameters.put(name, request.getParameter(name));
            }
        }
        
        return parameters;
    }
    
    /**
     * Check if a header is sensitive and should be redacted
     */
    private boolean isSensitiveHeader(String name) {
        name = name.toLowerCase();
        return name.contains("auth") || 
               name.contains("token") || 
               name.contains("password") || 
               name.contains("secret") ||
               name.contains("key") ||
               name.contains("cookie");
    }
    
    /**
     * Check if a parameter is sensitive and should be redacted
     */
    private boolean isSensitiveParameter(String name) {
        name = name.toLowerCase();
        return name.contains("password") || 
               name.contains("token") || 
               name.contains("secret") || 
               name.contains("auth") ||
               name.contains("key") ||
               name.contains("credit") ||
               name.contains("card");
    }
}

