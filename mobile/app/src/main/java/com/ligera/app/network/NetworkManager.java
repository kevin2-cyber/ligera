package com.ligera.app.network;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ligera.app.network.interceptor.NetworkConnectionInterceptor;
import com.ligera.app.repository.base.ErrorHandler;
import com.ligera.app.util.AppExecutors;
import com.ligera.app.util.Resource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * Centralized manager for network operations and state.
 * Provides a singleton interface for network interactions and coordinates
 * between NetworkMonitor, NetworkErrorHandler, and RetrofitClient.
 */
public class NetworkManager {
    private static final String TAG = "NetworkManager";

    // Singleton instance
    private static volatile NetworkManager instance;

    // Network components
    private final NetworkMonitor networkMonitor;
    private final NetworkErrorHandler networkErrorHandler;
    private final RetrofitClient retrofitClient;
    private final NetworkConnectionInterceptor networkConnectionInterceptor;

    // Network state
    private final MutableLiveData<NetworkMonitor.NetworkState> networkStateLiveData;
    private NetworkMonitor.NetworkState currentNetworkState;

    // Request queue for offline mode
    private final Map<String, QueuedRequest<?>> requestQueue;

    // Background executor for network operations
    private final AppExecutors appExecutors;

    // Private constructor (singleton pattern)
    private NetworkManager(@NonNull Context context) {
        Context appContext = context.getApplicationContext();

        // Initialize components
        this.networkConnectionInterceptor = new NetworkConnectionInterceptor(appContext);
        TokenManager tokenManager = TokenManager.getInstance(appContext);
        this.retrofitClient = RetrofitClient.getInstance(tokenManager, networkConnectionInterceptor);
        this.networkErrorHandler = new NetworkErrorHandler(appContext);
        this.networkMonitor = new NetworkMonitor(appContext);

        // Initialize state
        this.networkStateLiveData = new MutableLiveData<>();
        this.currentNetworkState = networkMonitor.getCurrentNetworkState();
        this.networkStateLiveData.setValue(currentNetworkState);

        // Initialize request queue
        this.requestQueue = new ConcurrentHashMap<>();

        // Initialize executor
        this.appExecutors = new AppExecutors();

        // Register for network callbacks
        setupNetworkCallbacks();
    }

    /**
     * Get singleton instance of NetworkManager
     *
     * @param context Application context
     * @return NetworkManager instance
     */
    public static NetworkManager getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (NetworkManager.class) {
                if (instance == null) {
                    instance = new NetworkManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * Setup network callbacks to monitor network state changes
     */
    private void setupNetworkCallbacks() {
        networkMonitor.addCallback(networkState -> {
            Log.d(TAG, "Network state changed: " + networkState);

            // Update current state
            currentNetworkState = networkState;
            networkStateLiveData.postValue(networkState);

            // Process queued requests if network becomes available
            if (networkState.isConnected() && !requestQueue.isEmpty()) {
                processQueuedRequests();
            }
        });
    }

    /**
     * Process queued requests when network becomes available
     */
    private void processQueuedRequests() {
        Log.d(TAG, "Processing " + requestQueue.size() + " queued requests");

        // Create a copy to avoid concurrent modification
        QueuedRequest<?>[] requests = requestQueue.values().toArray(new QueuedRequest[0]);

        // Process each request
        for (QueuedRequest<?> request : requests) {
            if (request != null) {
                String requestId = request.getRequestId();
                Log.d(TAG, "Processing queued request: " + requestId);

                // Remove from queue
                requestQueue.remove(requestId);

                // Execute the request
                appExecutors.networkIO().execute(() -> {
                    try {
                        request.execute();
                    } catch (Exception e) {
                        Log.e(TAG, "Error executing queued request: " + requestId, e);
                    }
                });
            }
        }
    }

    /**
     * Attach the NetworkManager to a lifecycle owner
     *
     * @param lifecycleOwner The lifecycle owner
     */
    public void attachToLifecycle(@NonNull LifecycleOwner lifecycleOwner) {
        lifecycleOwner.getLifecycle().addObserver(networkMonitor);
    }

    /**
     * Detach the NetworkManager from a lifecycle owner
     *
     * @param lifecycleOwner The lifecycle owner
     */
    public void detachFromLifecycle(@NonNull LifecycleOwner lifecycleOwner) {
        lifecycleOwner.getLifecycle().removeObserver(networkMonitor);
    }

    /**
     * Start monitoring network changes
     */
    public void startNetworkMonitoring() {
        networkMonitor.startMonitoring();
    }

    /**
     * Stop monitoring network changes
     */
    public void stopNetworkMonitoring() {
        networkMonitor.stopMonitoring();
    }

    /**
     * Get current network state
     *
     * @return Current NetworkState
     */
    @NonNull
    public NetworkMonitor.NetworkState getCurrentNetworkState() {
        return currentNetworkState;
    }

    /**
     * Get LiveData of network state
     *
     * @return LiveData of NetworkState
     */
    @NonNull
    public LiveData<NetworkMonitor.NetworkState> getNetworkState() {
        return networkStateLiveData;
    }

    /**
     * Check if network is currently connected
     *
     * @return true if connected, false otherwise
     */
    public boolean isNetworkConnected() {
        return currentNetworkState.isConnected();
    }

    /**
     * Check if network is currently on WiFi
     *
     * @return true if on WiFi, false otherwise
     */
    public boolean isOnWifi() {
        return currentNetworkState.isWifi();
    }

    /**
     * Check if network is currently on mobile data
     *
     * @return true if on mobile data, false otherwise
     */
    public boolean isOnMobileData() {
        return currentNetworkState.isMobile();
    }

    /**
     * Check if current connection is metered
     *
     * @return true if metered, false otherwise
     */
    public boolean isConnectionMetered() {
        return currentNetworkState.isMetered();
    }

    /**
     * Get the network error handler
     *
     * @return NetworkErrorHandler
     */
    @NonNull
    public ErrorHandler getErrorHandler() {
        return networkErrorHandler;
    }

    /**
     * Get Retrofit client for API V1
     *
     * @return RetrofitClient for API V1
     */
    public RetrofitClient getRetrofitClient() {
        return retrofitClient;
    }

    /**
     * Add a request to the offline queue
     *
     * @param requestId Unique ID for the request
     * @param request The request to queue
     * @param <T> Type of request result
     */
    public <T> void queueRequest(String requestId, QueuedRequest<T> request) {
        requestQueue.put(requestId, request);
        Log.d(TAG, "Request queued: " + requestId);
    }

    /**
     * Remove a request from the offline queue
     *
     * @param requestId ID of the request to remove
     */
    public void removeQueuedRequest(String requestId) {
        requestQueue.remove(requestId);
        Log.d(TAG, "Request removed from queue: " + requestId);
    }

    /**
     * Clear all queued requests
     */
    public void clearRequestQueue() {
        requestQueue.clear();
        Log.d(TAG, "Request queue cleared");
    }

    /**
     * Get the number of queued requests
     *
     * @return Number of queued requests
     */
    public int getQueuedRequestCount() {
        return requestQueue.size();
    }

    /**
     * Execute a network operation with automatic error handling and offline queueing
     *
     * @param operation The operation to execute
     * @param <T> Type of operation result
     * @return LiveData of Resource with operation result
     */
    public <T> LiveData<Resource<T>> executeNetworkOperation(NetworkOperation<T> operation) {
        MutableLiveData<Resource<T>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        // Check network connectivity
        if (!isNetworkConnected()) {
            Log.d(TAG, "No network connection, queuing request: " + operation.getOperationName());

            // Queue the request if it's queueable
            if (operation.isQueueable()) {
                String requestId = operation.getOperationName() + "_" + System.currentTimeMillis();
                QueuedRequest<T> queuedRequest = new QueuedRequest<>(requestId, operation, result);
                queueRequest(requestId, queuedRequest);

                result.setValue(Resource.offline(null, "Operation queued: " + operation.getOperationName()));
            } else {
                result.setValue(Resource.error("No network connection", null));
            }

            return result;
        }

        // Execute on background thread
        appExecutors.networkIO().execute(() -> {
            try {
                // Execute the operation
                T data = operation.execute();
                result.postValue(Resource.success(data));
            } catch (Exception e) {
                Log.e(TAG, "Error executing network operation: " + operation.getOperationName(), e);

                // Handle error
                boolean handled = networkErrorHandler.handleError(e, operation.getOperationName());

                if (handled) {
                    // If error was handled (e.g., retried), try again
                    try {
                        T data = operation.execute();
                        result.postValue(Resource.success(data));
                    } catch (Exception retryError) {
                        Log.e(TAG, "Error executing retry for network operation: " +
                                operation.getOperationName(), retryError);
                        result.postValue(Resource.error(
                                networkErrorHandler.getErrorMessage(retryError), null));
                    }
                } else {
                    // If error wasn't handled, return error resource
                    result.postValue(Resource.error(networkErrorHandler.getErrorMessage(e), null));
                }
            }
        });

        return result;
    }

    /**
     * Interface for network operations
     *
     * @param <T> Type of operation result
     */
    public interface NetworkOperation<T> {
        /**
         * Execute the operation
         *
         * @return Operation result
         * @throws Exception if operation fails
         */
        T execute() throws Exception;

        /**
         * Get operation name (for logging)
         *
         * @return Operation name
         */
        String getOperationName();

        /**
         * Check if operation can be queued for offline execution
         *
         * @return true if queueable, false otherwise
         */
        boolean isQueueable();
    }

    /**
     * Class for queued requests
     *
     * @param <T> Type of request result
     */
    private static class QueuedRequest<T> {
        private final String requestId;
        private final NetworkOperation<T> operation;
        private final MutableLiveData<Resource<T>> result;

        public QueuedRequest(String requestId, NetworkOperation<T> operation,
                           MutableLiveData<Resource<T>> result) {
            this.requestId = requestId;
            this.operation = operation;
            this.result = result;
        }

        public String getRequestId() {
            return requestId;
        }

        public void execute() throws Exception {
            result.postValue(Resource.loading(null));
            T data = operation.execute();
            result.postValue(Resource.success(data));
        }
    }
}
