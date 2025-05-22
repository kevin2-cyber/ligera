package com.ligera.app.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.transition.Explode;
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
import com.ligera.app.viewmodel.DetailActivityViewModel;

import java.util.Objects;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    Product product;

    public static final String PRODUCT_ID = "product_id";
    public static final String PRODUCT_IMAGE = "product_image";
    public static final String PRODUCT_NAME = "product_name";
    public static final String PRODUCT_DESCRIPTION = "product_description";
    public static final String PRODUCT_PRICE = "product_price";
    public static final String PRODUCT_QUANTITY ="product_quantity";
    public static final String CATEGORY_ID = "category_id";
    public static final String PRODUCT_BRAND = "product_brand";
    public static final String PRODUCT_SIZE = "product_size";

    DetailActivityViewModel  viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        getWindow().setExitTransition(new Explode());
        setContentView(R.layout.activity_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        product = new Product();
        binding.setProduct(product);


        viewModel = new ViewModelProvider(this).get(DetailActivityViewModel.class);
        binding.setViewmodel(viewModel);


        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        if (intent.hasExtra(PRODUCT_ID)) {
            product.setName(intent.getStringExtra(PRODUCT_NAME));
            Glide.with(this)
                    .load(intent.getIntExtra(PRODUCT_IMAGE, R.drawable.attire))
                    .apply(new RequestOptions().fitCenter())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(binding.ivProduct);
            product.setBrand(intent.getStringExtra(PRODUCT_BRAND));
            product.setDescription(intent.getIntExtra(PRODUCT_DESCRIPTION, R.string.contents));
            product.setQuantity(intent.getIntExtra(PRODUCT_QUANTITY, 1));
            product.setPrice(intent.getStringExtra(PRODUCT_PRICE));
            product.setSize(intent.getStringExtra(PRODUCT_SIZE));
        } else {
            Toast.makeText(this, "No data sent", Toast.LENGTH_LONG).show();
        }

        viewModel.getCounter().observe(this, counter -> binding.numberText.setText(String.valueOf(counter)));


        binding.chipGroup.setOnCheckedStateChangeListener((chipGroup, list) -> {
            if (list.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Nothing selected",Toast.LENGTH_LONG).show();
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i : list) {
                    Chip chip = findViewById(i);
                    chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.emerald, Resources.getSystem().newTheme())));
                    stringBuilder.append(",").append(chip.getText());
                }
                Toast.makeText(getApplicationContext(), "selected sizes: " + stringBuilder.toString().replaceFirst(",",""), Toast.LENGTH_LONG).show();
            }
        });




    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.fav_icon) {
            MenuItem menuItem = binding.toolbar.getMenu().findItem(R.id.fav_icon);
            CheckBox checkBox = (CheckBox) menuItem.getActionView();
            assert checkBox != null;
            checkBox.setBackgroundResource(R.drawable.sl_favourite);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_app_bar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}