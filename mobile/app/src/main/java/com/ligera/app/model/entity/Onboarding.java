package com.ligera.app.model.entity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class Onboarding{
    int image;

    public Onboarding() {}

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    @NonNull
    @Override
    public String toString() {
        return "Onboarding{" +
                "image=" + image +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Onboarding onboarding = (Onboarding) obj;
        return image == onboarding.image;
    }

    @Override
    public int hashCode() {
        return Objects.hash(image);
    }
}
