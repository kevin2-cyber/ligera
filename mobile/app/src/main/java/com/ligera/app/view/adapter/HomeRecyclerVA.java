package com.ligera.app.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ligera.app.R;
import com.ligera.app.databinding.ProductItemBinding;
import com.ligera.app.model.entity.Product;
import com.ligera.app.view.util.ProductDiffCallback;

import java.util.ArrayList;
import java.util.List;

public class HomeRecyclerVA extends RecyclerView.Adapter<HomeRecyclerVA.HomeRecyclerVH> {
    private OnProductItemClickListener listener;
    private ArrayList<Product> productList;
    private Context context;

    public HomeRecyclerVA(Context context, ArrayList<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public void setFilterList(List<Product> products) {
        productList = (ArrayList<Product>) products;
        productList.addAll(products);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HomeRecyclerVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProductItemBinding itemBinding = DataBindingUtil.inflate(

                LayoutInflater.from(parent.getContext()), R.layout.product_item, parent, false

        );
        return new HomeRecyclerVH(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRecyclerVH holder, int position) {
        Product currentProduct = productList.get(position);
        holder.binding.setProduct(currentProduct);
        holder.setItemImage(currentProduct.getImage());
        holder.binding.productCard.startAnimation(AnimationUtils.loadAnimation(holder.binding.productCard.getContext(), R.anim.anim_two));
    }


    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public void setProductList(ArrayList<Product> newProductList) {
        final DiffUtil.DiffResult result =
                DiffUtil.calculateDiff(new ProductDiffCallback(productList, newProductList),false);
        productList = newProductList;
        result.dispatchUpdatesTo(HomeRecyclerVA.this);
    }

    public class HomeRecyclerVH extends RecyclerView.ViewHolder {
        private ProductItemBinding binding;

        public HomeRecyclerVH(@NonNull ProductItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(view -> {
                int clickedPosition = getAdapterPosition();
                if (listener != null && clickedPosition != RecyclerView.NO_POSITION) {
                    listener.onProductItemClick(productList.get(clickedPosition));
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
