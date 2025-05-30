package com.ligera.app.network;

import android.content.Context;
import android.net.ConnectivityManager;

import androidx.annotation.NonNull;

import com.ligera.app.network.interceptor.NetworkConnectionInterceptor;
import com.ligera.app.repository.base.ErrorHandler;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

import timber.log.Timber;

/**
 * Implementation of ErrorHandler for network-related errors.
 * Handles different types of network exceptions and provides retry logic
 * for recoverable errors.
 */
public class NetworkErrorHandler implements ErrorHandler {
    private static final String TAG = "NetworkErrorHandler";
    
    private final Context context;
    private final ConnectivityManager connectivityManager;
    private final AtomicInteger retryCounter = new AtomicInteger(0);
    private final int maxRetries;
    private final long retryDelayMs;
    private final float backoffFactor;
    
    /**
     * Constructor with default retry settings
     * 
     * @param context Application context
     */
    public NetworkErrorHandler(@NonNull Context context) {
        this(context, ApiConfig.MAX_RETRIES, ApiConfig.RETRY_DELAY_MS, ApiConfig.RETRY_BACKOFF_FACTOR);
    }
    
    /**
     * Constructor with custom retry settings
     * 
     * @param context Application context
     * @param maxRetries Maximum number of retry attempts
     * @param retryDelayMs Base delay between retries in milliseconds
     * @param backoffFactor Factor to increase delay for subsequent retries
     */
    public NetworkErrorHandler(@NonNull Context context, int maxRetries, long retryDelayMs, float backoffFactor) {
        this.context = context.getApplicationContext();
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.maxRetries = maxRetries;
        this.retryDelayMs = retryDelayMs;
        this.backoffFactor = backoffFactor;
    }
    
    @Override
    public boolean handleError(Throwable error, String operationName) {
        ErrorType errorType = getErrorType(error);
        
        logError(error, operationName);
        
        // If the error is recoverable and we haven't exceeded max retries
        if (isErrorRecoverable(error) && retryCounter.get() < maxRetries) {
            Timber.d("Attempting to recover from %s for operation %s (attempt %d/%d)",
                    errorType, operationName, retryCounter.incrementAndGet(), maxRetries);
            
            Runnable recoveryAction = getRecoveryAction(error);
            if (recoveryAction != null) {
                recoveryAction.run();
                return true;
            }
        }
        
        // Reset retry counter if we're not retrying
        retryCounter.set(0);
        return false;
    }
    
    @Override
    public String getErrorMessage(Throwable error) {
        if (error instanceof NetworkConnectionInterceptor.NoNetworkException) {
            return ApiConfig.ErrorMessages.NO_NETWORK;
        } else if (error instanceof NetworkConnectionInterceptor.TimeoutException) {
            return ApiConfig.ErrorMessages.CONNECTION_TIMEOUT;
        } else if (error instanceof NetworkConnectionInterceptor.WeakConnectionException) {
            return ApiConfig.ErrorMessages.WEAK_CONNECTION;
        } else if (error instanceof NetworkConnectionInterceptor.NetworkException) {
            return error.getMessage();
        } else if (error instanceof SocketTimeoutException) {
            return ApiConfig.ErrorMessages.CONNECTION_TIMEOUT;
        } else if (error instanceof UnknownHostException) {
            return ApiConfig.ErrorMessages.NO_NETWORK;
        } else if (error instanceof IOException) {
            return ApiConfig.ErrorMessages.NETWORK_ERROR;
        } else {
            return error.getMessage();
        }
    }
    
    @Override
    public ErrorType getErrorType(Throwable error) {
        if (error instanceof NetworkConnectionInterceptor.NoNetworkException) {
            return ErrorType.NETWORK;
        } else if (error instanceof NetworkConnectionInterceptor.TimeoutException) {
            return ErrorType.TIMEOUT;
        } else if (error instanceof NetworkConnectionInterceptor.WeakConnectionException) {
            return ErrorType.NETWORK;
        } else if (error instanceof SocketTimeoutException) {
            return ErrorType.TIMEOUT;
        } else if (error instanceof UnknownHostException) {
            return ErrorType.NETWORK;
        } else if (error instanceof IOException) {
            return ErrorType.NETWORK;
        } else {
            return ErrorType.UNKNOWN;
        }
    }
    
    @Override
    public boolean isErrorRecoverable(Throwable error) {
        ErrorType errorType = getErrorType(error);
        
        switch (errorType) {
            case TIMEOUT:
                // Timeout errors are generally recoverable with a retry
                return true;
                
            case NETWORK:
                // Network errors may be recoverable if the connection is restored
                return hasNetworkConnection();
                
            case AUTHORIZATION:
            case AUTHENTICATION:
                // Auth errors might be recoverable with token refresh
                return true;
                
            case RATE_LIMIT:
                // Rate limit errors are recoverable after waiting
                return true;
                
            default:
                return false;
        }
    }
    
    @Override
    public Runnable getRecoveryAction(Throwable error) {
        ErrorType errorType = getErrorType(error);
        
        switch (errorType) {
            case TIMEOUT:
            case NETWORK:
                return createRetryWithBackoffAction();
                
            case RATE_LIMIT:
                return createRateLimitRecoveryAction();
                
            default:
                return null;
        }
    }
    
    @Override
    public void logError(Throwable error, String operationName) {
        ErrorType errorType = getErrorType(error);
        
        switch (errorType) {
            case NETWORK:
                Timber.w("Network error during %s: %s", operationName, error.getMessage());
                break;
                
            case TIMEOUT:
                Timber.w("Timeout during %s: %s", operationName, error.getMessage());
                break;
                
            case RATE_LIMIT:
                Timber.w("Rate limit exceeded during %s", operationName);
                break;
                
            default:
                Timber.e(error, "Error during %s: %s", operationName, error.getMessage());
                break;
        }
    }
    
    /**
     * Check if the device currently has a network connection
     * 
     * @return true if connected, false otherwise
     */
    private boolean hasNetworkConnection() {
        NetworkConnectionInterceptor interceptor = new NetworkConnectionInterceptor(context);
        return interceptor.isConnected();
    }
    
    /**
     * Create a retry action with exponential backoff
     * 
     * @return Runnable that waits with exponential backoff
     */
    private Runnable createRetryWithBackoffAction() {
        return () -> {
            int attempt = retryCounter.get();
            long delayMs = (long) (retryDelayMs * Math.pow(backoffFactor, attempt - 1));
            
            Timber.d("Waiting %d ms before retry attempt %d", delayMs, attempt);
            
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Timber.w("Retry delay interrupted");
            }
        };
    }
    
    /**
     * Create a recovery action for rate limit errors
     * 
     * @return Runnable that waits for a rate limit window
     */
    private Runnable createRateLimitRecoveryAction() {
        return () -> {
            // Wait longer for rate limit (at least 5 seconds)
            Timber.d("Waiting for rate limit window");
            
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Timber.w("Rate limit wait interrupted");
            }
        };
    }
    
    /**
     * Reset the retry counter
     */
    public void resetRetryCounter() {
        retryCounter.set(0);
    }
    
    /**
     * Get the current retry count
     * 
     * @return current retry count
     */
    public int getRetryCount() {
        return retryCounter.get();
    }
    
    /**
     * Get the maximum allowed retries
     * 
     * @return maximum retries
     */
    public int getMaxRetries() {
        return maxRetries;
    }
}

