package com.ligera.app.repository.base;

import java.util.concurrent.TimeUnit;

/**
 * Policy for determining when to refresh data from network
 */
public class RefreshPolicy {
    /**
     * Refresh strategies
     */
    public enum Strategy {
        /**
         * Always refresh from network, ignore cache
         */
        ALWAYS_REFRESH,
        
        /**
         * Refresh if cache is stale (based on cacheTimeout)
         */
        REFRESH_IF_STALE,
        
        /**
         * Load from cache first, then refresh from network
         */
        CACHE_AND_NETWORK,
        
        /**
         * Load from cache only, never refresh from network
         */
        CACHE_ONLY,
        
        /**
         * Load from network only, don't cache
         */
        NETWORK_ONLY
    }
    
    private Strategy strategy;
    private long cacheTimeout;
    private long rateLimit;
    private int maxRetries;
    private boolean retryOnError;
    
    /**
     * Create a new refresh policy with default values
     */
    public RefreshPolicy() {
        this.strategy = Strategy.REFRESH_IF_STALE;
        this.cacheTimeout = TimeUnit.HOURS.toMillis(24);
        this.rateLimit = TimeUnit.SECONDS.toMillis(5);
        this.maxRetries = 3;
        this.retryOnError = true;
    }
    
    /**
     * Create a new refresh policy with specific values
     *
     * @param strategy Refresh strategy
     * @param cacheTimeout Cache timeout in milliseconds
     * @param rateLimit Rate limit in milliseconds
     * @param maxRetries Maximum number of retries
     * @param retryOnError Whether to retry on error
     */
    public RefreshPolicy(Strategy strategy, long cacheTimeout, long rateLimit, int maxRetries, boolean retryOnError) {
        this.strategy = strategy;
        this.cacheTimeout = cacheTimeout;
        this.rateLimit = rateLimit;
        this.maxRetries = maxRetries;
        this.retryOnError = retryOnError;
    }
    
    /**
     * Create a refresh policy with a specific strategy
     *
     * @param strategy Refresh strategy
     * @return A new refresh policy
     */
    public static RefreshPolicy withStrategy(Strategy strategy) {
        RefreshPolicy policy = new RefreshPolicy();
        policy.strategy = strategy;
        return policy;
    }
    
    /**
     * Create a refresh policy with a specific cache timeout
     *
     * @param timeout Cache timeout
     * @param unit Time unit
     * @return A new refresh policy
     */
    public static RefreshPolicy withCacheTimeout(long timeout, TimeUnit unit) {
        RefreshPolicy policy = new RefreshPolicy();
        policy.cacheTimeout = unit.toMillis(timeout);
        return policy;
    }
    
    /**
     * Create a refresh policy with a specific rate limit
     *
     * @param limit Rate limit
     * @param unit Time unit
     * @return A new refresh policy
     */
    public static RefreshPolicy withRateLimit(long limit, TimeUnit unit) {
        RefreshPolicy policy = new RefreshPolicy();
        policy.rateLimit = unit.toMillis(limit);
        return policy;
    }
    
    /**
     * Get the refresh strategy
     *
     * @return The refresh strategy
     */
    public Strategy getStrategy() {
        return strategy;
    }
    
    /**
     * Set the refresh strategy
     *
     * @param strategy The refresh strategy to set
     */
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
    
    /**
     * Get the cache timeout in milliseconds
     *
     * @return The cache timeout in milliseconds
     */
    public long getCacheTimeout() {
        return cacheTimeout;
    }
    
    /**
     * Set the cache timeout
     *
     * @param cacheTimeout Cache timeout in milliseconds
     */
    public void setCacheTimeout(long cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
    }
    
    /**
     * Set the cache timeout
     *
     * @param timeout Cache timeout
     * @param unit Time unit
     */
    public void setCacheTimeout(long timeout, TimeUnit unit) {
        this.cacheTimeout = unit.toMillis(timeout);
    }
    
    /**
     * Get the rate limit in milliseconds
     *
     * @return The rate limit in milliseconds
     */
    public long getRateLimit() {
        return rateLimit;
    }
    
    /**
     * Set the rate limit
     *
     * @param rateLimit Rate limit in milliseconds
     */
    public void setRateLimit(long rateLimit) {
        this.rateLimit = rateLimit;
    }
    
    /**
     * Set the rate limit
     *
     * @param limit Rate limit
     * @param unit Time unit
     */
    public void setRateLimit(long limit, TimeUnit unit) {
        this.rateLimit = unit.toMillis(limit);
    }
    
    /**
     * Get the maximum number of retries
     *
     * @return The maximum number of retries
     */
    public int getMaxRetries() {
        return maxRetries;
    }
    
    /**
     * Set the maximum number of retries
     *
     * @param maxRetries The maximum number of retries
     */
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    /**
     * Check if should retry on error
     *
     * @return True if should retry on error, false otherwise
     */
    public boolean isRetryOnError() {
        return retryOnError;
    }
    
    /**
     * Set whether to retry on error
     *
     * @param retryOnError Whether to retry on error
     */
    public void setRetryOnError(boolean retryOnError) {
        this.retryOnError = retryOnError;
    }
    
    /**
     * Check if data is stale based on last refresh time
     *
     * @param lastRefreshTime Last refresh time in milliseconds
     * @return True if data is stale, false otherwise
     */
    public boolean isDataStale(long lastRefreshTime) {
        return System.currentTimeMillis() - lastRefreshTime > cacheTimeout;
    }
    
    /**
     * Check if should fetch from network based on strategy and last refresh time
     *
     * @param lastRefreshTime Last refresh time in milliseconds
     * @return True if should fetch from network, false otherwise
     */
    public boolean shouldFetchFromNetwork(long lastRefreshTime) {
        switch (strategy) {
            case ALWAYS_REFRESH:
                return true;
            case REFRESH_IF_STALE:
                return isDataStale(lastRefreshTime);
            case CACHE_AND_NETWORK:
                return true;
            case CACHE_ONLY:
                return false;
            case NETWORK_ONLY:
                return true;
            default:
                return false;
        }
    }
}

