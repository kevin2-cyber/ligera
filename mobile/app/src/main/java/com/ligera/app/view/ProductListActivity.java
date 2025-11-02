package com.ligera.app.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.ligera.app.R;
import com.ligera.app.model.entity.Product;
import com.ligera.app.network.NetworkManager;
import com.ligera.app.network.NetworkMonitor;
import com.ligera.app.network.service.ProductApiService;
import com.ligera.app.repository.util.Resource;
import com.ligera.app.view.adapter.HomeProductAdapter;
import com.ligera.app.view.util.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Example activity demonstrating NetworkManager usage for product listing
 */
public class ProductListActivity extends AppCompatActivity {
    
    // UI Components
    private RecyclerView recyclerView;
    private HomeProductAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View loadingView;
    private View emptyView;
    private View offlineView;
    
    // Network components
    private NetworkManager networkManager;
    private boolean isOfflineSnackbarShowing = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        
        // Initialize UI components
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        loadingView = findViewById(R.id.loadingView);
        emptyView = findViewById(R.id.emptyView);
        offlineView = findViewById(R.id.offlineView);
        
        // Initialize adapter
        adapter = new HomeProductAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        // Set up pull-to-refresh
        swipeRefreshLayout.setOnRefreshListener(this::refreshProducts);
        
        // Initialize network manager
        initializeNetworkManager();
        
        // Set up RecyclerView animations
        AnimationUtils.setupRecyclerViewSlideAnimation(recyclerView);
        
        // Load products
        loadProducts();
    }
    
    /**
     * Initialize NetworkManager and set up network monitoring
     */
    private void initializeNetworkManager() {
        // 1. Get NetworkManager instance
        networkManager = NetworkManager.getInstance(getApplicationContext());
        
        // 2. Attach to lifecycle for automatic management
        networkManager.attachToLifecycle(this);
        
        // 3. Monitor network state changes
        networkManager.getNetworkState().observe(this, this::handleNetworkStateChange);
    }
    
    /**
     * Handle network state changes
     * 
     * @param networkState Current network state
     */
    private void handleNetworkStateChange(@NonNull NetworkMonitor.NetworkState networkState) {
        Timber.d("Network state changed: %s", networkState);
        
        if (networkState.isConnected()) {
            // Network is available
            if (isOfflineSnackbarShowing) {
                isOfflineSnackbarShowing = false;
                
                // Show reconnected message
                Snackbar.make(recyclerView, R.string.network_reconnected, Snackbar.LENGTH_SHORT).show();
                
                // If we have pending requests, they will be processed automatically
                if (networkManager.getQueuedRequestCount() > 0) {
                    Toast.makeText(this, 
                            getString(R.string.processing_offline_requests, networkManager.getQueuedRequestCount()),
                            Toast.LENGTH_SHORT).show();
                }
                
                // If we don't have any products, try loading them
                if (adapter.getItemCount() == 0) {
                    loadProducts();
                }
            }
            
            // Hide offline view
            if (offlineView.getVisibility() == View.VISIBLE) {
                AnimationUtils.crossFade(recyclerView, offlineView);
            }
            
        } else {
            // Network is unavailable
            if (!isOfflineSnackbarShowing) {
                isOfflineSnackbarShowing = true;
                
                // Show offline message
                Snackbar snackbar = Snackbar.make(recyclerView, 
                        R.string.network_offline, 
                        Snackbar.LENGTH_INDEFINITE);
                
                snackbar.setAction(R.string.dismiss, v -> isOfflineSnackbarShowing = false);
                snackbar.show();
                
                // Show offline view if we don't have cached data
                if (adapter.getItemCount() == 0) {
                    AnimationUtils.crossFade(offlineView, recyclerView);
                }
            }
        }
        
        // Update UI based on network type
        if (networkState.isMetered() && networkState.isMobile()) {
            // On mobile data - we might want to disable auto-loading images or other heavy content
            adapter.setLoadHighResImages(false);
        } else {
            // On WiFi or unmetered connection
            adapter.setLoadHighResImages(true);
        }
    }
    
    /**
     * Load products from network or cache
     */
    private void loadProducts() {
        // Show loading state
        AnimationUtils.showLoading(loadingView, recyclerView);
        
        // Execute network operation
        networkManager.executeNetworkOperation(new NetworkManager.NetworkOperation<List<Product>>() {
            @Override
            public List<Product> execute() throws Exception {
                // Get Retrofit service
                ProductApiService apiService = networkManager.getRetrofitClient()
                        .getProductClient()
                        .create(ProductApiService.class);
                
                // Make API call
                Call<List<Product>> call = apiService.getProducts();
                Response<List<Product>> response = call.execute();
                
                if (!response.isSuccessful()) {
                    throw new Exception("Error: " + response.code());
                }
                
                return response.body() != null ? response.body() : new ArrayList<>();
            }
            
            @Override
            public String getOperationName() {
                return "loadProducts";
            }
            
            @Override
            public boolean isQueueable() {
                // This operation can be queued for offline execution
                return true;
            }
        }).observe(this, new Observer<Resource<List<Product>>>() {
            @Override
            public void onChanged(Resource<List<Product>> resource) {
                // Hide loading state
                swipeRefreshLayout.setRefreshing(false);
                
                switch (resource.getStatus()) {
                    case SUCCESS:
                        // Update UI with data
                        AnimationUtils.hideLoading(loadingView, recyclerView);
                        updateProductList(resource.getData());
                        break;
                        
                    case ERROR:
                        // Show error
                        AnimationUtils.hideLoading(loadingView, recyclerView);
                        Snackbar.make(recyclerView, 
                                resource.getMessage(), 
                                Snackbar.LENGTH_LONG).show();
                        
                        // Show empty view if we don't have any data
                        if (adapter.getItemCount() == 0) {
                            AnimationUtils.crossFade(emptyView, recyclerView);
                        }
                        break;
                        
                    case LOADING:
                        // Loading state is handled by AnimationUtils.showLoading above
                        break;
                        
                    case OFFLINE:
                        // Request has been queued for later execution
                        AnimationUtils.hideLoading(loadingView, null);
                        Snackbar.make(recyclerView, 
                                R.string.operation_queued, 
                                Snackbar.LENGTH_LONG).show();
                        
                        // Show offline view
                        if (adapter.getItemCount() == 0) {
                            AnimationUtils.crossFade(offlineView, recyclerView);
                        }
                        break;
                }
            }
        });
    }
    
    /**
     * Refresh products (called from pull-to-refresh)
     */
    private void refreshProducts() {
        // If offline, show message and return
        if (!networkManager.isNetworkConnected()) {
            swipeRefreshLayout.setRefreshing(false);
            Snackbar.make(recyclerView, 
                    R.string.offline_cannot_refresh, 
                    Snackbar.LENGTH_SHORT).show();
            return;
        }
        
        // Force load products from network
        loadProducts();
    }
    
    /**
     * Update product list in the adapter
     * 
     * @param products List of products
     */
    private void updateProductList(List<Product> products) {
        if (products != null && !products.isEmpty()) {
            // Update adapter
            adapter.setProducts(products);
            
            // Show RecyclerView
            if (recyclerView.getVisibility() != View.VISIBLE) {
                AnimationUtils.crossFade(recyclerView, emptyView);
                AnimationUtils.crossFade(recyclerView, offlineView);
            }
            
            // Animate items
            AnimationUtils.animateRecyclerViewItems(recyclerView, 100);
        } else {
            // Show empty view
            AnimationUtils.crossFade(emptyView, recyclerView);
        }
    }
    
    @Override
    protected void onDestroy() {
        // Cleanup
        networkManager.detachFromLifecycle(this);
        super.onDestroy();
    }
}

