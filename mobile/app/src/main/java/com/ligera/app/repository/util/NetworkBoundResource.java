package com.ligera.app.repository.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.ligera.app.network.model.ApiResponse;
import com.ligera.app.util.Resource;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A generic class that can provide a resource backed by both the SQLite database and the network.
 * <p>
 * This class coordinates between the local database and the network to provide a consistent data
 * access strategy with proper caching, error handling, rate limiting, and retry policies.
 *
 * @param <ResultType> Type for the Resource data from database.
 * @param <RequestType> Type for the API response.
 */
public abstract class NetworkBoundResource<ResultType, RequestType> {
    private static final String TAG = "NetworkBoundResource";
    
    // Default cache timeout in milliseconds (24 hours)
    private static final long DEFAULT_CACHE_TIMEOUT = TimeUnit.HOURS.toMillis(24);
    
    // Default rate limit for API calls in milliseconds (5 seconds)
    private static final long DEFAULT_RATE_LIMIT = TimeUnit.SECONDS.toMillis(5);
    
    // Maximum number of retries
    private static final int MAX_RETRIES = 3;
    
    // Executor for background operations
    private static final Executor DISK_IO = Executors.newFixedThreadPool(4);
    
    // Handler for main thread operations
    private static final Handler MAIN_THREAD_HANDLER = new Handler(Looper.getMainLooper());

    // Result to be observed
    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();
    
    // Track if a fetch is in progress
    private final AtomicBoolean fetchInProgress = new AtomicBoolean(false);
    
    // Track the last fetch time
    private final AtomicLong lastFetchTime = new AtomicLong(0);
    
    // Current retry count
    private int retryCount = 0;
    
    // Configuration options
    private long cacheTimeout = DEFAULT_CACHE_TIMEOUT;
    private long rateLimit = DEFAULT_RATE_LIMIT;
    private boolean forceRefresh = false;
    
    /**
     * Creates a new NetworkBoundResource instance with default configuration
     */
    @MainThread
    protected NetworkBoundResource() {
        init();
    }
    
    /**
     * Creates a new NetworkBoundResource instance with custom configuration
     *
     * @param cacheTimeout How long the cache is valid in milliseconds
     * @param rateLimit Minimum time between API calls in milliseconds
     * @param forceRefresh Whether to force a refresh from network
     */
    @MainThread
    protected NetworkBoundResource(long cacheTimeout, long rateLimit, boolean forceRefresh) {
        this.cacheTimeout = cacheTimeout;
        this.rateLimit = rateLimit;
        this.forceRefresh = forceRefresh;
        init();
    }
    
    /**
     * Initialize the resource
     */
    private void init() {
        // Start by loading from database
        result.setValue(Resource.loading(null));
        
        // Load data from DB
        LiveData<ResultType> dbSource = loadFromDb();
        
        // Add the database source
        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            
            if (forceRefresh || shouldFetch(data)) {
                // Fetch data from network
                fetchFromNetwork(dbSource);
            } else {
                // Use data from database
                result.addSource(dbSource, newData -> setValue(Resource.success(newData)));
            }
        });
    }
    
    /**
     * Fetch data from network and save to database
     *
     * @param dbSource LiveData source from database
     */
    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        // Check if a fetch is already in progress
        if (fetchInProgress.get()) {
            Log.d(TAG, "Fetch already in progress, skipping...");
            return;
        }
        
        // Check rate limiting
        long currentTime = System.currentTimeMillis();
        long timeSinceLastFetch = currentTime - lastFetchTime.get();
        
        if (timeSinceLastFetch < rateLimit) {
            Log.d(TAG, "Rate limit hit, waiting " + (rateLimit - timeSinceLastFetch) + "ms...");
            // Schedule a retry after the rate limit
            MAIN_THREAD_HANDLER.postDelayed(() -> fetchFromNetwork(dbSource), rateLimit - timeSinceLastFetch);
            return;
        }
        
        // Set fetch in progress
        fetchInProgress.set(true);
        
        // Show loading and use data from DB if available
        result.addSource(dbSource, newData -> setValue(Resource.loading(newData)));
        
        // Create API call
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();
        
        // Observe API response
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);
            
            // Update last fetch time
            lastFetchTime.set(System.currentTimeMillis());
            
            // Process response
            if (response.isSuccessful()) {
                // Reset retry count on success
                retryCount = 0;
                
                // Save result to DB on background thread
                DISK_IO.execute(() -> {
                    try {
                        saveCallResult(processResponse(response));
                        
                        // Dispatch on main thread
                        MAIN_THREAD_HANDLER.post(() -> {
                            // Fetch is completed
                            fetchInProgress.set(false);
                            
                            // Re-attach DB source
                            result.addSource(loadFromDb(), newData -> setValue(Resource.success(newData)));
                        });
                    } catch (Exception e) {
                        // Handle error during saving
                        Log.e(TAG, "Error saving API result to database", e);
                        MAIN_THREAD_HANDLER.post(() -> {
                            // Fetch is completed
                            fetchInProgress.set(false);
                            
                            // Report error
                            onFetchFailed(e);
                            result.addSource(dbSource, newData -> setValue(Resource.error(e.getMessage(), newData)));
                        });
                    }
                });
            } else {
                // Handle API error
                onFetchFailed(new Exception(response.errorMessage));
                
                // Attempt retry if applicable
                if (shouldRetry() && retryCount < MAX_RETRIES) {
                    retryCount++;
                    Log.d(TAG, "Retrying API call, attempt " + retryCount + " of " + MAX_RETRIES);
                    
                    // Schedule retry after a backoff period
                    long backoffTime = calculateBackoffTime(retryCount);
                    MAIN_THREAD_HANDLER.postDelayed(() -> {
                        fetchInProgress.set(false);
                        fetchFromNetwork(dbSource);
                    }, backoffTime);
                    return;
                }
                
                // Fetch is completed
                fetchInProgress.set(false);
                
                // Use data from DB and report error
                result.addSource(dbSource, newData ->
                {
                    assert response.errorMessage != null;
                    setValue(Resource.error(response.errorMessage, newData));
                });
            }
        });
    }
    
    /**
     * Calculate exponential backoff time based on retry count
     *
     * @param retry Current retry attempt
     * @return Backoff time in milliseconds
     */
    private long calculateBackoffTime(int retry) {
        return (long) (1000 * Math.pow(2, retry - 1));
    }
    
    /**
     * Set value on the result LiveData
     *
     * @param newValue New value to set
     */
    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if (!Objects.equals(result.getValue(), newValue)) {
            result.setValue(newValue);
        }
    }
    
    /**
     * Process the response from the API
     *
     * @param response API response
     * @return Processed response data
     */
    @WorkerThread
    protected RequestType processResponse(ApiResponse<RequestType> response) {
        return response.body;
    }
    
    /**
     * Determine if a retry should be attempted
     *
     * @return True if the operation should be retried
     */
    @MainThread
    protected boolean shouldRetry() {
        return true; // Default behavior is to retry
    }
    
    /**
     * Called when a network request fails
     *
     * @param error The error that caused the failure
     */
    protected void onFetchFailed(Exception error) {
        // Can be overridden by subclasses for custom error handling
        Log.e(TAG, "Network request failed", error);
    }
    
    /**
     * Returns a LiveData object that represents the resource
     *
     * @return LiveData of the resource
     */
    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }
    
    /**
     * Force a refresh from the network
     */
    public void refresh() {
        forceRefresh = true;
        retryCount = 0;
        fetchInProgress.set(false);
        init();
    }
    
    /**
     * Check if data is stale based on the cache timeout
     *
     * @param timestamp Last refresh timestamp
     * @return True if data is stale and should be refreshed
     */
    protected boolean isDataStale(long timestamp) {
        return System.currentTimeMillis() - timestamp > cacheTimeout;
    }
    
    /**
     * Get the last refresh time from a list of entities
     *
     * @param data List of entities with timestamps
     * @param <T> Entity type with a getLastRefreshed method
     * @return The most recent timestamp or 0 if none available
     */
    protected <T> long getLastRefreshTime(List<T> data) {
        if (data == null || data.isEmpty()) {
            return 0;
        }
        
        long maxTime = 0;
        
        for (T item : data) {
            if (item instanceof HasLastRefreshed) {
                long time = ((HasLastRefreshed) item).getLastRefreshed();
                if (time > maxTime) {
                    maxTime = time;
                }
            }
        }
        
        return maxTime;
    }
    
    /**
     * Interface for entities that track their last refresh time
     */
    public interface HasLastRefreshed {
        long getLastRefreshed();
    }
    
    //-------------------------------------------------------------------------------------------
    // Abstract methods to be implemented by subclasses
    //-------------------------------------------------------------------------------------------
    
    /**
     * Decide whether to fetch data from the network or not
     *
     * @param data Current data from database
     * @return True if data should be fetched from network
     */
    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);
    
    /**
     * Save the result of the API response into the database
     *
     * @param item Response data to save
     */
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);
    
    /**
     * Get the cached data from the database
     *
     * @return LiveData of cached data
     */
    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();
    
    /**
     * Create the API call
     *
     * @return LiveData of the API response
     */
    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();
}