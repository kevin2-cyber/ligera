package com.ligera.backend.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Filter to collect metrics about API requests
 */
@Slf4j
@Component
@Order(2) // Execute after rate limiting
public class MetricsFilter implements Filter {

    // Metrics counters
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong successfulRequests = new AtomicLong(0);
    private final AtomicLong failedRequests = new AtomicLong(0);
    private final Map<String, AtomicLong> endpointHits = new ConcurrentHashMap<>();
    private final Map<Integer, AtomicLong> statusCodeCounts = new ConcurrentHashMap<>();
    
    // Response time metrics
    private final AtomicLong totalResponseTimeMs = new AtomicLong(0);
    private final AtomicLong requestCount = new AtomicLong(0);
    private final AtomicLong maxResponseTimeMs = new AtomicLong(0);
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Skip metrics for static resources
        if (isStaticResource(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Start timing
        Instant start = Instant.now();
        
        // Track total requests
        totalRequests.incrementAndGet();
        
        // Track endpoint hits
        String endpoint = getEndpoint(httpRequest);
        endpointHits.computeIfAbsent(endpoint, k -> new AtomicLong()).incrementAndGet();
        
        try {
            // Process the request
            chain.doFilter(request, response);
            
            // Record status code
            int statusCode = httpResponse.getStatus();
            statusCodeCounts.computeIfAbsent(statusCode, k -> new AtomicLong()).incrementAndGet();
            
            // Record success/failure
            if (statusCode >= 200 && statusCode < 400) {
                successfulRequests.incrementAndGet();
            } else {
                failedRequests.incrementAndGet();
            }
        } finally {
            // Calculate response time
            long responseTimeMs = Duration.between(start, Instant.now()).toMillis();
            
            // Update response time metrics
            totalResponseTimeMs.addAndGet(responseTimeMs);
            requestCount.incrementAndGet();
            
            // Update max response time (thread-safe way)
            updateMaxResponseTime(responseTimeMs);
            
            // Log request metrics (only for non-static resources)
            if (log.isDebugEnabled()) {
                log.debug("Request: {} {} - Status: {} - Time: {} ms", 
                        httpRequest.getMethod(), 
                        httpRequest.getRequestURI(), 
                        httpResponse.getStatus(), 
                        responseTimeMs);
            }
            
            // Periodically log summary metrics (every 100 requests)
            if (requestCount.get() % 100 == 0) {
                logMetricsSummary();
            }
        }
    }
    
    /**
     * Update max response time in a thread-safe way
     */
    private void updateMaxResponseTime(long responseTimeMs) {
        long currentMax;
        do {
            currentMax = maxResponseTimeMs.get();
            if (responseTimeMs <= currentMax) {
                // Current response time is not a new max
                break;
            }
        } while (!maxResponseTimeMs.compareAndSet(currentMax, responseTimeMs));
    }
    
    /**
     * Log a summary of the current metrics
     */
    private void logMetricsSummary() {
        long total = requestCount.get();
        if (total == 0) return;
        
        double avgResponseTime = (double) totalResponseTimeMs.get() / total;
        
        log.info("API Metrics Summary:");
        log.info("  Total Requests: {}", totalRequests.get());
        log.info("  Successful Requests: {}", successfulRequests.get());
        log.info("  Failed Requests: {}", failedRequests.get());
        log.info("  Average Response Time: {:.2f} ms", avgResponseTime);
        log.info("  Max Response Time: {} ms", maxResponseTimeMs.get());
        
        // Log top 5 endpoints by hit count
        log.info("  Top Endpoints:");
        endpointHits.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue().get(), e1.getValue().get()))
                .limit(5)
                .forEach(entry -> log.info("    {} - {} hits", entry.getKey(), entry.getValue().get()));
        
        // Log status code distribution
        log.info("  Status Code Distribution:");
        statusCodeCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> log.info("    {} - {} requests", entry.getKey(), entry.getValue().get()));
    }
    
    /**
     * Get a normalized endpoint path
     * Groups similar endpoints like /users/123 and /users/456 as /users/{id}
     */
    private String getEndpoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        
        // Normalize ID patterns in URLs
        // This transforms patterns like /api/v1/users/123 to /api/v1/users/{id}
        String normalizedUri = uri.replaceAll("/\\d+", "/{id}");
        
        return method + " " + normalizedUri;
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
     * Reset all metrics (for admin purposes)
     */
    public void resetMetrics() {
        totalRequests.set(0);
        successfulRequests.set(0);
        failedRequests.set(0);
        endpointHits.clear();
        statusCodeCounts.clear();
        totalResponseTimeMs.set(0);
        requestCount.set(0);
        maxResponseTimeMs.set(0);
    }
}

