package com.ligera.app.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ligera.app.R;
import com.ligera.app.model.Onboarding;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    List<Onboarding> onboardings;
    Context context;

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

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_onboarding);
        }
        void setOnboardingData(Onboarding onboarding) {
            imageView.setImageResource(onboarding.getImage());
        }
    }
}
