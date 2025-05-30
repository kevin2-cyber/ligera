package com.ligera.app.util;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Global executor pools for the whole application.
 * Grouping tasks like this avoids the effects of task starvation (e.g., disk reads don't wait
 * behind webservice requests).
 */
@Singleton
public class AppExecutors {

    private static final int THREAD_COUNT = 3;

    private final Executor diskIO;
    private final Executor networkIO;
    private final Executor mainThread;

    /**
     * Create AppExecutors with default thread configuration
     */
    public AppExecutors() {
        this(Executors.newSingleThreadExecutor(), new ThreadPoolExecutor(3,
                5, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()),
                new MainThreadExecutor());
    }

    /**
     * Create AppExecutors with custom thread configuration
     */
    public AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    /**
     * Get executor for disk I/O operations
     */
    public Executor diskIO() {
        return diskIO;
    }

    /**
     * Get executor for network operations
     */
    public Executor networkIO() {
        return networkIO;
    }

    /**
     * Get executor for main thread operations
     */
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

