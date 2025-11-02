package com.ligera.app.network.interceptor;

import android.util.Log;

import androidx.annotation.NonNull;

import com.ligera.app.network.ApiConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ErrorHandlingInterceptor implements Interceptor {
    private static final String TAG = "ErrorHandlingInterceptor";

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        IOException exception = null;

        int tryCount = 0;
        while (tryCount < ApiConfig.MAX_RETRIES) {
            try {
                response = chain.proceed(request);
                // If the response is successful or a client/server error, we don't need to retry
                if (response.isSuccessful() || (response.code() >= 400 && response.code() < 600)) {
                    return response;
                }
            } catch (IOException e) {
                Log.e(TAG, "Request failed, attempt " + (tryCount + 1), e);
                exception = e;
                if (!isRetryable(e)) {
                    // If the error is not retryable, throw it immediately
                    throw e;
                }
                // If it is retryable, increment the counter and the loop will continue
                tryCount++;
                try {
                    // Apply exponential backoff
                    long delay = (long) (ApiConfig.RETRY_DELAY_MS * Math.pow(ApiConfig.RETRY_BACKOFF_FACTOR, tryCount - 1));
                    Log.d(TAG, "Waiting for " + delay + "ms before retrying.");
                    Thread.sleep(delay);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Retry interrupted", interruptedException);
                }
            }
        }

        // If we're here, it means we've exhausted retries for an IOException
        if (exception != null) {
            throw exception;
        }

        // This should not be reached, but as a fallback, return the last response
        return response;
    }

    /**
     * Determines if an exception is retryable.
     * @param e The exception to check.
     * @return true if the request should be retried, false otherwise.
     */
    private boolean isRetryable(IOException e) {
        String message = e.getMessage();
        if (message == null) return false;
        
        return message.contains("timeout") 
                || message.contains("connection")
                || message.contains("refused")
                || message.contains("reset")
                || message.contains("unreachable");
    }
}
