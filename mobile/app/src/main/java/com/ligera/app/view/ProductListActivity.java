package com.ligera.app.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.ligera.app.R;
import com.ligera.app.network.NetworkManager;
import com.ligera.app.network.NetworkMonitor;
import com.ligera.app.view.adapter.HomeProductAdapter;
import com.ligera.app.view.util.AnimationUtils;
import com.ligera.app.viewmodel.ProductViewModel;
import com.ligera.app.viewmodel.state.ProductState;

/**
 * Activity for displaying a list of products, demonstrating ViewModel and NetworkManager usage.
 */
public class ProductListActivity extends AppCompatActivity {

    private static final String TAG = "ProductListActivity";

    // UI Components
    private RecyclerView recyclerView;
    private HomeProductAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View loadingView;
    private View emptyView;
    private View offlineView;

    // ViewModel and Network components
    private ProductViewModel productViewModel;
    private NetworkManager networkManager;
    private boolean isOfflineSnackbarShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Initialize UI components
        setupUI();

        // Initialize ViewModel
        setupViewModel();

        // Initialize NetworkManager for network state monitoring
        initializeNetworkManager();
    }

    private void setupUI() {
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        loadingView = findViewById(R.id.loadingView);
        emptyView = findViewById(R.id.emptyView);
        offlineView = findViewById(R.id.offlineView);

        // Initialize adapter
        adapter = new HomeProductAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Set up pull-to-refresh
        swipeRefreshLayout.setOnRefreshListener(this::refreshProducts);

        // Set up RecyclerView animations
        AnimationUtils.setupRecyclerViewSlideAnimation(recyclerView);
    }

    private void setupViewModel() {
        // Get a new or existing ViewModel from the ViewModelProvider
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        // Initialize the ViewModel (which can trigger initial data loads)
        productViewModel.init(this);

        // Observe the product state for changes
        productViewModel.getProductState().observe(this, this::handleProductStateChange);
    }

    private void handleProductStateChange(ProductState state) {
        swipeRefreshLayout.setRefreshing(state.isRefreshing());

        if (state.isLoading() && !state.isRefreshing()) {
            AnimationUtils.showLoading(loadingView, recyclerView);
        } else {
            AnimationUtils.hideLoading(loadingView, recyclerView);
        }

        if (state.getProducts() != null) {
            adapter.submitData(getLifecycle(), state.getProducts());
        }

        if (state.getError() != null) {
            handleError(state.getError());
        }

        if (state.isOffline()) {
            handleOfflineState();
        }
    }

    /**
     * Initialize NetworkManager and set up network monitoring
     */
    private void initializeNetworkManager() {
        networkManager = NetworkManager.getInstance(getApplicationContext());
        networkManager.attachToLifecycle(this);
        networkManager.getNetworkState().observe(this, this::handleNetworkStateChange);
    }

    /**
     * Handle network state changes
     *
     * @param networkState Current network state
     */
    private void handleNetworkStateChange(@NonNull NetworkMonitor.NetworkState networkState) {
        Log.d(TAG, "Network state changed: " + networkState);

        if (networkState.isConnected()) {
            if (isOfflineSnackbarShowing) {
                isOfflineSnackbarShowing = false;
                Snackbar.make(recyclerView, R.string.network_reconnected, Snackbar.LENGTH_SHORT).show();

                if (networkManager.getQueuedRequestCount() > 0) {
                    Toast.makeText(this,
                            getString(R.string.processing_offline_requests, networkManager.getQueuedRequestCount()),
                            Toast.LENGTH_SHORT).show();
                }
                // ViewModel will handle reloading if necessary
            }
            if (offlineView.getVisibility() == View.VISIBLE) {
                AnimationUtils.crossFade(recyclerView, offlineView);
            }
        } else {
            if (!isOfflineSnackbarShowing) {
                isOfflineSnackbarShowing = true;
                Snackbar snackbar = Snackbar.make(recyclerView, R.string.network_offline, Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.dismiss, v -> isOfflineSnackbarShowing = false);
                snackbar.show();
            }
        }
    }

    /**
     * Refresh products (called from pull-to-refresh)
     */
    private void refreshProducts() {
        if (!networkManager.isNetworkConnected()) {
            swipeRefreshLayout.setRefreshing(false);
            Snackbar.make(recyclerView, R.string.offline_cannot_refresh, Snackbar.LENGTH_SHORT).show();
            return;
        }
        productViewModel.refreshData();
    }

    private void handleError(String errorMessage) {
        Snackbar.make(recyclerView, errorMessage, Snackbar.LENGTH_LONG).show();
        if (adapter.getItemCount() == 0) {
            AnimationUtils.crossFade(emptyView, recyclerView);
        }
    }

    private void handleOfflineState() {
        Snackbar.make(recyclerView, R.string.operation_queued, Snackbar.LENGTH_LONG).show();
        if (adapter.getItemCount() == 0) {
            AnimationUtils.crossFade(offlineView, recyclerView);
        }
    }

    @Override
    protected void onDestroy() {
        networkManager.detachFromLifecycle(this);
        super.onDestroy();
    }
}
