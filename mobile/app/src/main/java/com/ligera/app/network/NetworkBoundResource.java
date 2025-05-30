package com.ligera.app.network;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.ligera.app.network.model.ApiResponse;
import com.ligera.app.util.AppExecutors;
import com.ligera.app.util.Resource;

/**
 * A generic class that can provide a resource backed by both the database and network.
 * It implements the "Single Source of Truth" pattern.
 *
 * @param <ResultType> Type for the Resource data.
 * @param <RequestType> Type for the API response.

