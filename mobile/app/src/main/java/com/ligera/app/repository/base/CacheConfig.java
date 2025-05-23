package com.ligera.app.repository.base;

import java.util.concurrent.TimeUnit;

/**
 * Configuration for repository caching behavior
 */
public class CacheConfig {
    
    /**
     * Cache storage types
     */
    public enum StorageType {
        /**
         * In-memory cache only
         */
        MEMORY,
        
        /**
         * Database cache
         */
        DATABASE,
        
        /**
         * Shared preferences cache
         */
        PREFERENCES,
        
        /**
         * File cache
         */
        FILE,
        
        /**
         * No caching
         */
        NONE
    }
    
    /**
     * Cache eviction policies
     */
    public enum EvictionPolicy {
        /**
         * Least recently used
         */
        LRU,
        
        /**
         * First in, first out
         */
        FIFO,
        
        /**
         * Time-based expiration
         */
        TIMED,
        
        /**
         * Never evict
         */
        NEVER
    }
    
    private StorageType storageType;
    private EvictionPolicy evictionPolicy;
    private long timeout;
    private int maxSize;
    private boolean encryptCache;
    
    /**
     * Create a new cache configuration with default values
     */
    public CacheConfig() {
        this.storageType = StorageType.DATABASE;
        

