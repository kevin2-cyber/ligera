package com.ligera.app.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ligera.app.R;
import com.ligera.app.model.entity.Onboarding;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private final List<Onboarding> onboardings;
    private final Context context;

    public OnboardingAdapter(List<Onboarding> onboardings, Context context) {
        this.onboardings = onboardings;
        this.context = context;
    }


    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.onboarding_item, parent, false);
        return new OnboardingViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.setOnboardingData(onboardings.get(position));
    }

    @Override
    public int getItemCount() {
        return onboardings.size();
    }

    public static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_onboarding);
        }
        void setOnboardingData(Onboarding onboarding) {
            Glide.with(imageView.getContext())
                            .load(onboarding.getImage())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(false)
                            .into(imageView);
        }
    }
}
