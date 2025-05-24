package com.ligera.backend.service;

import com.ligera.backend.config.RateLimitConfig;
import com.ligera.backend.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing rate limiting buckets and logic
 */
@Slf4j
@Service
public class RateLimitService {

    private final RateLimitConfig rateLimitConfig;
    private final Bandwidth anonymousBandwidth;
    private final Bandwidth authenticatedBandwidth;
    
    @Autowired
    public RateLimitService(RateLimitConfig rateLimitConfig, 
                           Bandwidth anonymousBandwidth,
                           Bandwidth authenticatedBandwidth) {
        this.rateLimitConfig = rateLimitConfig;
        this.anonymousBandwidth = anonymousBandwidth;
        this.authenticatedBandwidth = authenticatedBandwidth;
    }
    
    // Cache of rate limit buckets by key (IP or user ID)
    private final Map<String, Bucket> anonymousBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> authenticatedBuckets = new ConcurrentHashMap<>();
    
    /**
     * Check if a request should be allowed based on rate limits
     * 
     * @param userId user ID for authenticated users, null for anonymous
     * @param ipAddress client IP address (used for anonymous users)
     * @return true if request is allowed, false if rate limit exceeded
     */
    public boolean checkRateLimit(String userId, String ipAddress) {
        if (!rateLimitConfig.isEnabled()) {
            return true;
        }
        
        // Get the appropriate bucket based on authentication status
        Bucket bucket = getBucket(userId, ipAddress);
        
        // Try to consume a token
        return bucket.tryConsume(1);
    }
    
    /**
     * Consume a token from the rate limit bucket and throw exception if limit exceeded
     * 
     * @param userId user ID for authenticated users, null for anonymous
     * @param ipAddress client IP address (used for anonymous users)
     * @throws RateLimitExceededException if rate limit is exceeded
     */
    public void consumeRateLimit(String userId, String ipAddress) {
        if (!rateLimitConfig.isEnabled()) {
            return;
        }
        
        // Check if rate limit is exceeded
        if (!checkRateLimit(userId, ipAddress)) {
            String requestSource = userId != null ? "user ID: " + userId : "IP: " + ipAddress;
            log.warn("Rate limit exceeded for {}", requestSource);
            throw new RateLimitExceededException();
        }
    }
    
    /**
     * Get the appropriate bucket for a request
     * 
     * @param userId user ID for authenticated users, null for anonymous
     * @param ipAddress client IP address (used for anonymous users)
     * @return the rate limit bucket
     */
    private Bucket getBucket(String userId, String ipAddress) {
        if (userId != null) {
            // Authenticated user - use user ID as key
            return authenticatedBuckets.computeIfAbsent(userId, 
                    id -> Bucket4j.builder().addLimit(authenticatedBandwidth).build());
        } else {
            // Anonymous user - use IP address as key
            return anonymousBuckets.computeIfAbsent(ipAddress, 
                    ip -> Bucket4j.builder().addLimit(anonymousBandwidth).build());
        }
    }
    
    /**
     * Clear all rate limit buckets (for testing/admin purposes)
     */
    public void clearBuckets() {
        anonymousBuckets.clear();
        authenticatedBuckets.clear();
    }
}

