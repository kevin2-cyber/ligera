package com.ligera.app.network.model.request;

import com.google.gson.annotations.SerializedName;

public record RefreshTokenRequest(@SerializedName("refreshToken") String refreshToken) {

    @Override
    public String refreshToken() {
        return refreshToken;
    }
}
