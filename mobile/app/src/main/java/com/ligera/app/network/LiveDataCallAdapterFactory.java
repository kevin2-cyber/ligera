package com.ligera.app.network;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.ligera.app.network.model.ApiResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * A factory for adapters for Retrofit that convert calls into LiveData objects.
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
            throw new IllegalArgumentException("Type must be a resource");
        }
        
        if (!(observableType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Resource must be parameterized");
        }
        
        Type bodyType = getParameterUpperBound(0, (ParameterizedType) observableType);
        return new LiveDataCallAdapter<>(bodyType);
    }
}

