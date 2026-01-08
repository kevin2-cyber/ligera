package com.ligera.app.view;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.chip.Chip;
import com.ligera.app.R;
import com.ligera.app.databinding.ActivityDetailBinding;
import com.ligera.app.model.entity.Product;
import com.ligera.app.util.Resource;
import com.ligera.app.viewmodel.DetailActivityViewModel;

import java.util.Objects;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;

    public static final String PRODUCT_ID = "product_id";

    DetailActivityViewModel viewModel;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        viewModel = new ViewModelProvider(this).get(DetailActivityViewModel.class);
        binding.setLifecycleOwner(this);
        binding.setViewmodel(viewModel);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        long productId = getIntent().getLongExtra(PRODUCT_ID, -1);
        if (productId != -1) {
            viewModel.getProductById(productId).observe(this, resource -> {
                if (resource != null && resource.data != null) {
                    currentProduct = resource.data;
                    updateProductDetails(currentProduct);
                } else if (resource != null && resource.status == Resource.Status.ERROR) {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No product ID found", Toast.LENGTH_LONG).show();
        }

        viewModel.getCounter().observe(this, counter -> binding.numberText.setText(String.valueOf(counter)));


        binding.chipGroup.setOnCheckedStateChangeListener((chipGroup, list) -> {
            if (list.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Nothing selected",Toast.LENGTH_LONG).show();
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i : list) {
                    Chip chip = findViewById(i);
                    chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.emerald, getTheme())));
                    stringBuilder.append(",").append(chip.getText());
                }
                Toast.makeText(getApplicationContext(), "selected sizes: " + stringBuilder.toString().replaceFirst(",",""), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateProductDetails(Product product) {
        binding.setProduct(product);
        Glide.with(this)
                .load(product.getImageUrl())
                .apply(new RequestOptions().fitCenter())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .into(binding.ivProduct);
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_app_bar_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.fav_icon);
        CheckBox checkBox = (CheckBox) menuItem.getActionView();
        if (currentProduct != null) {
            assert checkBox != null;
            checkBox.setChecked(currentProduct.isFavorite());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.setFavorite(currentProduct.getId(), isChecked));
        }
        return super.onCreateOptionsMenu(menu);
    }
}
