package com.ligera.app.util;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Global executor pools for the whole application.
 * Grouping tasks like this avoids the effects of task starvation (e.g., disk reads don't wait
 * behind webservice requests).
 */
public record AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {

    /**
     * Create AppExecutors with default thread configuration
     */
    public AppExecutors() {
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3),
                new MainThreadExecutor());
    }

    /**
     * Create AppExecutors with custom thread configuration
     */
    public AppExecutors {
    }

    /**
     * Get executor for disk I/O operations
     */
    @Override
    public Executor diskIO() {
        return diskIO;
    }

    /**
     * Get executor for network operations
     */
    @Override
    public Executor networkIO() {
        return networkIO;
    }

    /**
     * Get executor for main thread operations
     */
    @Override
    public Executor mainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
