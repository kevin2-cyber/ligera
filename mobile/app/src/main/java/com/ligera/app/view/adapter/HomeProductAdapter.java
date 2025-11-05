package com.ligera.app.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ligera.app.R;
import com.ligera.app.databinding.ProductItemBinding;
import com.ligera.app.model.entity.Product;

public class HomeProductAdapter extends PagingDataAdapter<Product, HomeProductAdapter.HomeRecyclerVH> {
    private OnProductItemClickListener listener;
    private final Context context;

    // Implement DiffUtil properly
    private static final DiffUtil.ItemCallback<Product> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public HomeProductAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    @NonNull
    @Override
    public HomeRecyclerVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProductItemBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.product_item,
                parent,
                false);
        return new HomeRecyclerVH(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRecyclerVH holder, int position) {
        Product currentProduct = getItem(position);
        if (currentProduct != null) {
            holder.binding.setProduct(currentProduct);
            holder.setItemImage(currentProduct.getImageUrl());
            holder.binding.productCard.startAnimation(AnimationUtils.loadAnimation(holder.binding.productCard.getContext(), R.anim.anim_one));
        } else {
            // Null item passed when placeholders are enabled.
            // Clear the view to prevent showing stale data.
            holder.binding.setProduct(null);
            Glide.with(holder.binding.imageView.getContext()).clear(holder.binding.imageView);
            holder.binding.productCard.clearAnimation();
        }
    }

    public class HomeRecyclerVH extends RecyclerView.ViewHolder {
        private final ProductItemBinding binding;

        public HomeRecyclerVH(@NonNull ProductItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(view -> {
                int clickedPosition = getBindingAdapterPosition();
                if (listener != null && clickedPosition != RecyclerView.NO_POSITION) {
                    Product product = getItem(clickedPosition);
                    if (product != null) {
                        listener.onProductItemClick(product);
                    }
                }
            });
        }

        public void setItemImage(String imageUrl) {
            Glide.with(binding.imageView.getContext())
                    .load(imageUrl)
                    .apply(new RequestOptions().fitCenter())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(binding.imageView);
        }
    }

    public interface OnProductItemClickListener {
        void onProductItemClick(Product product);
    }

    public void setListener(OnProductItemClickListener listener) {
        this.listener = listener;
    }
}
