package com.ligera.app.repository.base;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.ligera.app.util.Resource;

/**
 * Base interface for repository operations.
 * Provides standardized methods for data fetching, caching, error handling and resource management.
 *
 * @param <T> Entity type
 * @param <ID> ID type for entity
 */
public interface BaseRepository<T, ID> {

    /**
     * Fetch data from the repository with default refresh options
     *
     * @param id Entity ID
     * @return LiveData of Resource with entity
     */
    LiveData<Resource<T>> fetch(ID id);

    /**
     * Fetch data from the repository with specific refresh options
     *
     * @param id Entity ID
     * @param forceRefresh Whether to force a refresh from network
     * @return LiveData of Resource with entity
     */
    LiveData<Resource<T>> fetch(ID id, boolean forceRefresh);

    /**
     * Fetch a list of entities from the repository
     *
     * @return LiveData of Resource with list of entities
     */
    LiveData<Resource<? extends Iterable<T>>> fetchAll();

    /**
     * Fetch a list of entities from the repository with specific refresh options
     *
     * @param forceRefresh Whether to force a refresh from network
     * @return LiveData of Resource with list of entities
     */
    LiveData<Resource<? extends Iterable<T>>> fetchAll(boolean forceRefresh);

    /**
     * Get the refresh policy for this repository
     *
     * @return The refresh policy
     */
    @NonNull
    RefreshPolicy getRefreshPolicy();

    /**
     * Set the refresh policy for this repository
     *
     * @param refreshPolicy The refresh policy to set
     */
    void setRefreshPolicy(@NonNull RefreshPolicy refreshPolicy);

    /**
     * Get the error handler for this repository
     *
     * @return The error handler
     */
    @NonNull
    ErrorHandler getErrorHandler();

    /**
     * Set the error handler for this repository
     *
     * @param errorHandler The error handler to set
     */
    void setErrorHandler(@NonNull ErrorHandler errorHandler);

    /**
     * Get the cache configuration for this repository
     *
     * @return The cache configuration
     */
    @NonNull
    CacheConfig getCacheConfig();

    /**
     * Set the cache configuration for this repository
     *
     * @param cacheConfig The cache configuration to set
     */
    void setCacheConfig(@NonNull CacheConfig cacheConfig);

    /**
     * Handle an error that occurred during repository operations
     *
     * @param error The error to handle
     * @param operationName Name of the operation that failed
     * @return True if the error was handled, false otherwise
     */
    boolean handleError(Throwable error, String operationName);

    /**
     * Clean up resources used by this repository
     */
    void cleanupResources();

    /**
     * Invalidate cache for specific entity
     *
     * @param id Entity ID
     */
    void invalidateCache(ID id);

    /**
     * Invalidate all caches for this repository
     */
    void invalidateAllCaches();

    /**
     * Force refresh data for specific entity
     *
     * @param id Entity ID
     * @return LiveData of Resource with entity
     */
    LiveData<Resource<T>> refresh(ID id);

    /**
     * Force refresh all data for this repository
     *
     * @return LiveData of Resource with list of entities
     */
    LiveData<Resource<? extends Iterable<T>>> refreshAll();

    /**
     * Create a new entity
     *
     * @param entity The entity to create
     * @return LiveData of Resource with created entity
     */
    LiveData<Resource<T>> create(T entity);

    /**
     * Update an existing entity
     *
     * @param entity The entity to update
     * @return LiveData of Resource with updated entity
     */
    LiveData<Resource<T>> update(T entity);

    /**
     * Delete an entity
     *
     * @param id Entity ID
     * @return LiveData of Resource with deleted entity ID
     */
    LiveData<Resource<ID>> delete(ID id);
}

