package com.ligera.app.network;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.ligera.app.network.model.ApiResponse;


/**
 * A Retrofit adapter factory for converting the Call into a LiveData of ApiResponse.
 */
public class LiveDataCallAdapterFactory extends CallAdapter.Factory {

    @Override
    public CallAdapter<?, ?> get(@NonNull Type returnType, @NonNull Annotation[] annotations, @NonNull Retrofit retrofit) {
        if (getRawType(returnType) != LiveData.class) {
            return null;
        }
        
        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        Class<?> rawObservableType = getRawType(observableType);
        
        if (rawObservableType != ApiResponse.class) {
            throw new IllegalArgumentException("Type must be a ApiResponse");
        }
        
        if (!(observableType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("ApiResponse must be parameterized");
        }
        
        Type bodyType = getParameterUpperBound(0, (ParameterizedType) observableType);
        return new LiveDataCallAdapter<>(bodyType);
    }
}