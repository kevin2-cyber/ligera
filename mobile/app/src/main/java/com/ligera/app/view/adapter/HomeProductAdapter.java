package com.ligera.app.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

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

public class HomeProductAdapter extends ListAdapter<Product, HomeProductAdapter.HomeRecyclerVH> {
    private OnProductItemClickListener listener;
    private final Context context;

    // Implement DiffUtil properly
    private static final DiffUtil.ItemCallback<Product> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
                    return oldItem.getProductId() == newItem.getProductId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public Object getChangePayload(@NonNull Product oldItem, @NonNull Product newItem) {
                    return super.getChangePayload(oldItem, newItem);
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
        holder.binding.setProduct(currentProduct);
        holder.setItemImage(currentProduct.getImage());
        holder.binding.productCard.startAnimation(AnimationUtils.loadAnimation(holder.binding.productCard.getContext(), R.anim.anim_one));
    }

    public class HomeRecyclerVH extends RecyclerView.ViewHolder {
        private final ProductItemBinding binding;

        public HomeRecyclerVH(@NonNull ProductItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(view -> {
                int clickedPosition = getAdapterPosition();
                if (listener != null && clickedPosition != RecyclerView.NO_POSITION) {
                    listener.onProductItemClick(getItem(clickedPosition));
                }
            });
        }

        public void setItemImage(int image) {
            Glide.with(binding.imageView.getContext())
                    .load(image)
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
