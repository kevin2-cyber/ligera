package com.ligera.app.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.ligera.app.network.interceptor.NetworkConnectionInterceptor;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

/**
 * A lifecycle-aware network monitor that provides real-time updates on network state changes.
 * Works with both modern and legacy Android APIs for network monitoring.
 */
public class NetworkMonitor implements DefaultLifecycleObserver {
    private static final String TAG = "NetworkMonitor";
    
    private final Context context;
    private final ConnectivityManager connectivityManager;
    private final Handler mainHandler;
    private final Set<NetworkCallback> networkCallbacks;
    private final AtomicBoolean isMonitoring;
    
    // For modern API
    private ConnectivityManager.NetworkCallback networkCallback;
    
    // For legacy API
    private NetworkBroadcastReceiver broadcastReceiver;
    
    // Current network state
    private NetworkState currentNetworkState;
    
    /**
     * Constructor
     * 
     * @param context Application context
     */
    public NetworkMonitor(@NonNull Context context) {
        this.context = context.getApplicationContext();
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.networkCallbacks = new CopyOnWriteArraySet<>();
        this.isMonitoring = new AtomicBoolean(false);
        
        // Initialize current network state
        this.currentNetworkState = getCurrentNetworkState();
    }
    
    /**
     * Start monitoring network changes
     */
    public void startMonitoring() {
        if (isMonitoring.compareAndSet(false, true)) {
            Timber.d("Starting network monitoring");
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                registerNetworkCallbackModern();
            } else {
                registerNetworkCallbackLegacy();
            }
        }
    }
    
    /**
     * Stop monitoring network changes
     */
    public void stopMonitoring() {
        if (isMonitoring.compareAndSet(true, false)) {
            Timber.d("Stopping network monitoring");
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                unregisterNetworkCallbackModern();
            } else {
                unregisterNetworkCallbackLegacy();
            }
        }
    }
    
    /**
     * Register for network callbacks - for modern API (N and above)
     */
    private void registerNetworkCallbackModern() {
        if (connectivityManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            
            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    Timber.d("Network available");
                    updateNetworkState();
                }
                
                @Override
                public void onLost(@NonNull Network network) {
                    Timber.d("Network lost");
                    updateNetworkState();
                }
                
                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities capabilities) {
                    Timber.d("Network capabilities changed");
                    updateNetworkState();
                }
            };
            
            try {
                connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
            } catch (Exception e) {
                Timber.e(e, "Error registering network callback");
            }
        }
    }
    
    /**
     * Unregister network callback - for modern API
     */
    private void unregisterNetworkCallbackModern() {
        if (connectivityManager != null && networkCallback != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
                networkCallback = null;
            } catch (Exception e) {
                Timber.e(e, "Error unregistering network callback");
            }
        }
    }
    
    /**
     * Register for network callbacks - for legacy API
     */
    private void registerNetworkCallbackLegacy() {
        if (broadcastReceiver == null) {
            broadcastReceiver = new NetworkBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(broadcastReceiver, intentFilter);
        }
    }
    
    /**
     * Unregister network callback - for legacy API
     */
    private void unregisterNetworkCallbackLegacy() {
        if (broadcastReceiver != null) {
            try {
                context.unregisterReceiver(broadcastReceiver);
                broadcastReceiver = null;
            } catch (Exception e) {
                Timber.e(e, "Error unregistering broadcast receiver");
            }
        }
    }
    
    /**
     * Add a network callback listener
     * 
     * @param callback The callback to add
     */
    public void addCallback(@NonNull NetworkCallback callback) {
        networkCallbacks.add(callback);
        
        // Immediately notify the new callback of the current state
        mainHandler.post(() -> callback.onNetworkStateChanged(currentNetworkState));
    }
    
    /**
     * Remove a network callback listener
     * 
     * @param callback The callback to remove
     */
    public void removeCallback(@NonNull NetworkCallback callback) {
        networkCallbacks.remove(callback);
    }
    
    /**
     * Update network state and notify callbacks
     */
    private void updateNetworkState() {
        // Get current network state
        NetworkState newState = getCurrentNetworkState();
        
        // If the state has changed, notify callbacks
        if (!newState.equals(currentNetworkState)) {
            Timber.d("Network state changed: %s -> %s", currentNetworkState, newState);
            
            // Update current state
            currentNetworkState = newState;
            
            // Notify callbacks on main thread
            mainHandler.post(() -> {
                for (NetworkCallback callback : networkCallbacks) {
                    callback.onNetworkStateChanged(currentNetworkState);
                }
            });
        }
    }
    
    /**
     * Get the current network state
     * 
     * @return Current NetworkState
     */
    @NonNull
    public NetworkState getCurrentNetworkState() {
        NetworkConnectionInterceptor.ConnectionType connectionType = getConnectionType();
        boolean isConnected = connectionType != NetworkConnectionInterceptor.ConnectionType.NONE;
        boolean isMetered = isMeteredConnection();
        int signalStrength = getSignalStrength();
        
        return new NetworkState(isConnected, connectionType, isMetered, signalStrength);
    }
    
    /**
     * Get the current connection type using NetworkConnectionInterceptor
     * 
     * @return ConnectionType
     */
    private NetworkConnectionInterceptor.ConnectionType getConnectionType() {
        NetworkConnectionInterceptor interceptor = new NetworkConnectionInterceptor(context);
        return interceptor.getConnectionType();
    }
    
    /**
     * Check if the current connection is metered (e.g., mobile data)
     * 
     * @return true if connection is metered
     */
    private boolean isMeteredConnection() {
        if (connectivityManager == null) {
            return false;
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Network activeNetwork = connectivityManager.getActiveNetwork();
                if (activeNetwork == null) {
                    return false;
                }
                
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                if (capabilities == null) {
                    return false;
                }
                
                return !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);
            } catch (SecurityException e) {
                Timber.e(e, "Security exception checking metered state");
                return isMeteredLegacy();
            }
        } else {
            return isMeteredLegacy();
        }
    }
    
    /**
     * Legacy method to check if connection is metered
     * 
     * @return true if connection is metered
     */
    @SuppressWarnings("deprecation")
    private boolean isMeteredLegacy() {
        if (connectivityManager == null) {
            return false;
        }
        
        return connectivityManager.isActiveNetworkMetered();
    }
    
    /**
     * Get signal strength (0-100)
     * 
     * @return signal strength or -1 if not available
     */
    private int getSignalStrength() {
        if (connectivityManager == null) {
            return -1;
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                Network activeNetwork = connectivityManager.getActiveNetwork();
                if (activeNetwork == null) {
                    return -1;
                }
                
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                if (capabilities == null) {
                    return -1;
                }
                
                return capabilities.getSignalStrength();
            } catch (Exception e) {
                Timber.e(e, "Error getting signal strength");
                return -1;
            }
        }
        
        // Signal strength not available on older API levels
        return -1;
    }
    
    // Lifecycle methods
    
    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        startMonitoring();
    }
    
    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        stopMonitoring();
    }
    
    /**
     * Legacy broadcast receiver for network changes
     */
    private class NetworkBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                Timber.d("Connectivity change detected via broadcast");
                updateNetworkState();
            }
        }
    }
    
    /**
     * Network callback interface
     */
    public interface NetworkCallback {
        /**
         * Called when network state changes
         * 
         * @param networkState The new network state
         */
        void onNetworkStateChanged(@NonNull NetworkState networkState);
    }
    
    /**
     * Network state data class
     */
    public static class NetworkState {
        private final boolean isConnected;
        private final NetworkConnectionInterceptor.ConnectionType connectionType;
        private final boolean isMetered;
        private final int signalStrength;
        
        public NetworkState(boolean isConnected, 
                          NetworkConnectionInterceptor.ConnectionType connectionType,
                          boolean isMetered, 
                          int signalStrength) {
            this.isConnected = isConnected;
            this.connectionType = connectionType;
            this.isMetered = isMetered;
            this.signalStrength = signalStrength;
        }
        
        public boolean isConnected() {
            return isConnected;
        }
        
        public NetworkConnectionInterceptor.ConnectionType getConnectionType() {
            return connectionType;
        }
        
        public boolean isMetered() {
            return isMetered;
        }
        
        public int getSignalStrength() {
            return signalStrength;
        }
        
        public boolean isWifi() {
            return connectionType == NetworkConnectionInterceptor.ConnectionType.WIFI;
        }
        
        public boolean isMobile() {
            return connectionType == NetworkConnectionInterceptor.ConnectionType.CELLULAR;
        }
        
        public boolean isEthernet() {
            return connectionType == NetworkConnectionInterceptor.ConnectionType.ETHERNET;
        }
        
        public boolean isVpn() {
            return connectionType == NetworkConnectionInterceptor.ConnectionType.VPN;
        }
        
        public boolean isStrongConnection() {
            return signalStrength > 50 || signalStrength == -1;
        }
        
        public boolean isWeakConnection() {
            return isConnected && signalStrength > 0 && signalStrength <= 30;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            
            NetworkState that = (NetworkState) o;
            
            if (isConnected != that.isConnected) return false;
            if (isMetered != that.isMetered) return false;
            if (signalStrength != that.signalStrength) return false;
            return connectionType == that.connectionType;
        }
        
        @Override
        public int hashCode() {
            int result = (isConnected ? 1 : 0);
            result = 31 * result + (connectionType != null ? connectionType.hashCode() : 0);
            result = 31 * result + (isMetered ? 1 : 0);
            result = 31 * result + signalStrength;
            return result;
        }
        
        @Override
        public String toString() {
            return "NetworkState{" +
                    "isConnected=" + isConnected +
                    ", connectionType=" + connectionType +
                    ", isMetered=" + isMetered +
                    ", signalStrength=" + signalStrength +
                    '}';
        }
    }
}

