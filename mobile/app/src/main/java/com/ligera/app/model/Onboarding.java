package com.ligera.app.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.ligera.app.BR;

public class Onboarding extends BaseObservable {
    int image;

    public Onboarding() {}

    @Bindable
    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
        notifyPropertyChanged(BR.image);
    }
}
