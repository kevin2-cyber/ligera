package com.ligera.app.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ligera.app.R;
import com.ligera.app.databinding.ProductItemBinding;
import com.ligera.app.model.entity.Product;

public class FavoritesAdapter extends ListAdapter<Product, FavoritesAdapter.FavoriteViewHolder> {

    private final Context context;
    private OnProductItemClickListener listener;

    public FavoritesAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    private static final DiffUtil.ItemCallback<Product> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProductItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.product_item,
                parent,
                false);
        return new FavoriteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Product product = getItem(position);
        if (product != null) {
            holder.bind(product);
        }
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private final ProductItemBinding binding;

        public FavoriteViewHolder(@NonNull ProductItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onProductItemClick(getItem(position));
                }
            });
        }

        public void bind(Product product) {
            binding.setProduct(product);
            Glide.with(context)
                    .load(product.getImageUrl())
                    .apply(new RequestOptions().fitCenter())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imageView);
            binding.executePendingBindings();
        }
    }

    public interface OnProductItemClickListener {
        void onProductItemClick(Product product);
    }

    public void setListener(OnProductItemClickListener listener) {
        this.listener = listener;
    }
}
