package com.ligera.app.network.interceptor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.ligera.app.network.ApiConfig;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp interceptor that checks for network connectivity before attempting a request
 * and provides appropriate error handling for various network states
 */
public class NetworkConnectionInterceptor implements Interceptor {
    private static final String TAG = "NetworkInterceptor";
    private final Context context;
    private final ConnectivityManager connectivityManager;

    /**
     * Network connection type enum
     */
    public enum ConnectionType {
        WIFI,
        CELLULAR,
        ETHERNET,
        VPN,
        OTHER,
        NONE
    }

    public NetworkConnectionInterceptor(Context context) {
        this.context = context;
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        ConnectionType connectionType = getConnectionType();
        
        // Check if there's any network connection
        if (connectionType == ConnectionType.NONE) {
            Log.d(TAG,"No network connection detected");
            throw new NoNetworkException(ApiConfig.ErrorMessages.NO_NETWORK);
        }
        
        Request.Builder builder = chain.request().newBuilder();
        Request originalRequest = chain.request();
        
        // Add connection type header for analytics
        builder.addHeader("X-Connection-Type", connectionType.name());
        
        // Modify request based on connection type
        if (connectionType == ConnectionType.CELLULAR) {
            // For mobile data, consider using caching to reduce data usage
            if (isMobileDataSavingEnabled()) {
                Log.d(TAG,"Mobile data with data saving - applying caching strategy");
                builder.cacheControl(new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build());
            } else {
                Log.d(TAG,"Mobile data connection detected");
            }
        } else if (connectionType == ConnectionType.WIFI) {
            Log.d(TAG,"WiFi connection detected");
            
            // For WiFi, we can use normal caching strategy
            if (!originalRequest.cacheControl().noCache()) {
                builder.cacheControl(new CacheControl.Builder()
                        .maxAge(2, TimeUnit.MINUTES)
                        .build());
            }
        }
        
        // Add timeout handling for different network conditions
        try {
            return chain.proceed(builder.build());
        } catch (SocketTimeoutException e) {
            // If we're on a weak connection, provide a more specific error message
            if (isWeakConnection()) {
                throw new WeakConnectionException(ApiConfig.ErrorMessages.WEAK_CONNECTION, e);
            } else {
                throw new TimeoutException(ApiConfig.ErrorMessages.CONNECTION_TIMEOUT, e);
            }
        } catch (IOException e) {
            // Enhance the error message based on the connection type
            String errorMessage;
            if (connectionType == ConnectionType.CELLULAR) {
                errorMessage = ApiConfig.ErrorMessages.MOBILE_DATA_ERROR;
            } else if (connectionType == ConnectionType.WIFI) {
                errorMessage = ApiConfig.ErrorMessages.WIFI_ERROR;
            } else {
                errorMessage = ApiConfig.ErrorMessages.NETWORK_ERROR;
            }
            
            Log.e(TAG, "Network error: " + errorMessage, e);
            throw new NetworkException(errorMessage, e);
        }
    }

    /**
     * Check if the device has an active network connection
     * 
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return getConnectionType() != ConnectionType.NONE;
    }
    
    /**
     * Get the type of the active network connection
     * 
     * @return ConnectionType enum value
     */
    public ConnectionType getConnectionType() {
        if (connectivityManager == null) {
            return ConnectionType.NONE;
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For Android M and above, use NetworkCapabilities
            try {
                android.net.Network activeNetwork = connectivityManager.getActiveNetwork();
                if (activeNetwork == null) {
                    return ConnectionType.NONE;
                }
                
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                if (capabilities == null) {
                    return ConnectionType.NONE;
                }
                
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return ConnectionType.WIFI;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return ConnectionType.CELLULAR;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    return ConnectionType.ETHERNET;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    return ConnectionType.VPN;
                } else {
                    return ConnectionType.OTHER;
                }
            } catch (SecurityException e) {
                // Fallback for permission issues
                Log.e(TAG, "Security exception checking network state", e);
                return fallbackConnectionCheck();
            }
        } else {
            // For older Android versions, use the deprecated NetworkInfo
            return fallbackConnectionCheck();
        }
    }
    
    /**
     * Fallback method to check connection for older Android versions
     * 
     * @return ConnectionType enum value
     */
    @SuppressWarnings("deprecation")
    private ConnectionType fallbackConnectionCheck() {
        try {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
                return ConnectionType.NONE;
            }
            
            int type = activeNetworkInfo.getType();
            if (type == ConnectivityManager.TYPE_WIFI) {
                return ConnectionType.WIFI;
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                return ConnectionType.CELLULAR;
            } else if (type == ConnectivityManager.TYPE_ETHERNET) {
                return ConnectionType.ETHERNET;
            } else if (type == ConnectivityManager.TYPE_VPN) {
                return ConnectionType.VPN;
            } else {
                return ConnectionType.OTHER;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in fallback connection check", e);
            return ConnectionType.NONE;
        }
    }
    
    /**
     * Check if mobile data saving is enabled
     * 
     * @return true if data saving is enabled, false otherwise
     */
    private boolean isMobileDataSavingEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                int status = connectivityManager.getRestrictBackgroundStatus();
                return status == ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED;
            } catch (Exception e) {
                Log.e(TAG, "Error checking data saver status", e);
                return false;
            }
        }
        return false;
    }
    
    /**
     * Check if the current connection is weak
     * 
     * @return true if connection is weak, false otherwise
     */
    private boolean isWeakConnection() {
        try {
            android.net.Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork == null) {
                return true;
            }

            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            if (capabilities == null) {
                return true;
            }

            // Check signal strength
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                int signalStrength = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    signalStrength = capabilities.getSignalStrength();
                }
                // Signal strength is from 0 to 100, with 100 being the best
                return signalStrength < 30; // Consider below 30 as weak
            }

            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error checking signal strength", e);
            return true; // Assume weak connection if we can't determine
        }
    }
    
    // Custom exception classes for different network errors
    
    /**
     * Exception for no network connection
     */
    public static class NoNetworkException extends IOException {
        public NoNetworkException(String message) {
            super(message);
        }
    }
    
    /**
     * Exception for timeout errors
     */
    public static class TimeoutException extends IOException {
        public TimeoutException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Exception for weak connection errors
     */
    public static class WeakConnectionException extends IOException {
        public WeakConnectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * General network exception
     */
    public static class NetworkException extends IOException {
        public NetworkException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
