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
    
    private final StorageType storageType;
    private final EvictionPolicy evictionPolicy;
    private final long timeout;
    private final int maxSize;
    private final boolean encryptCache;
    
    /**
     * Create a new cache configuration with default values
     */
    public CacheConfig() {
        this.storageType = StorageType.DATABASE;
        this.evictionPolicy = EvictionPolicy.TIMED;
        this.timeout = TimeUnit.HOURS.toMillis(24);
        this.maxSize = 1000; // Default max size of 1000 items
        this.encryptCache = false;
    }

    private CacheConfig(Builder builder) {
        this.storageType = builder.storageType;
        this.evictionPolicy = builder.evictionPolicy;
        this.timeout = builder.timeout;
        this.maxSize = builder.maxSize;
        this.encryptCache = builder.encryptCache;
    }
    
    // Getters
    public StorageType getStorageType() {
        return storageType;
    }

    public EvictionPolicy getEvictionPolicy() {
        return evictionPolicy;
    }

    public long getTimeout() {
        return timeout;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public boolean isEncryptCache() {
        return encryptCache;
    }

    // Builder class
    public static class Builder {
        private StorageType storageType = StorageType.DATABASE;
        private EvictionPolicy evictionPolicy = EvictionPolicy.TIMED;
        private long timeout = TimeUnit.HOURS.toMillis(24);
        private int maxSize = 1000;
        private boolean encryptCache = false;

        public Builder storageType(StorageType storageType) {
            this.storageType = storageType;
            return this;
        }

        public Builder evictionPolicy(EvictionPolicy evictionPolicy) {
            this.evictionPolicy = evictionPolicy;
            return this;
        }

        public Builder timeout(long duration, TimeUnit unit) {
            this.timeout = unit.toMillis(duration);
            return this;
        }

        public Builder maxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public Builder encryptCache(boolean encryptCache) {
            this.encryptCache = encryptCache;
            return this;
        }

        public CacheConfig build() {
            return new CacheConfig(this);
        }
    }
}
