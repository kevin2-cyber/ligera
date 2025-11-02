package com.ligera.app.model.converter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Room type converter for List<String> values
 * <p>
 * This converter allows Room to store List<String> values in the database as JSON Strings and
 * convert them back to List<String> objects when loading from the database.
 * </p>
 */
public class StringListConverter {
    private static final String TAG = StringListConverter.class.getSimpleName();
    
    // Use a static Gson instance for better performance
    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .create();
    
    // TypeToken for List<String>
    private static final Type LIST_STRING_TYPE = new TypeToken<List<String>>() {}.getType();
    
    /**
     * Convert a JSON string from the database to a List<String>
     *
     * @param value JSON string representation of a List<String>, or null
     * @return The corresponding List<String>, or an empty list if input was null or invalid
     */
    @TypeConverter
    @NonNull
    public static List<String> fromString(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            return gson.fromJson(value, LIST_STRING_TYPE);
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Error parsing JSON string to List<String>: " + value, e);
            // Return an empty list if there was an error parsing the JSON
            return new ArrayList<>();
        }
    }
    
    /**
     * Convert a List<String> to a JSON string for storage in the database
     *
     * @param list List<String> to convert, or null
     * @return JSON string representation of the List<String>, or null if input was null
     */
    @TypeConverter
    @Nullable
    public static String fromList(@Nullable List<String> list) {
        if (list == null) {
            return null;
        }
        
        if (list.isEmpty()) {
            return "[]";
        }
        
        try {
            return gson.toJson(list, LIST_STRING_TYPE);
        } catch (Exception e) {
            Log.e(TAG, "Error converting List<String> to JSON string", e);
            // Return an empty JSON array if there was an error converting to JSON
            return "[]";
        }
    }
    
    /**
     * Safely add an item to a List<String>, handling null lists
     *
     * @param list List to add to (may be null)
     * @param item Item to add
     * @return Updated list with the item added
     */
    @NonNull
    public static List<String> safeAdd(@Nullable List<String> list, @Nullable String item) {
        if (item == null) {
            return list != null ? list : new ArrayList<>();
        }
        
        List<String> result = list != null ? new ArrayList<>(list) : new ArrayList<>();
        result.add(item);
        return result;
    }
    
    /**
     * Safely remove an item from a List<String>, handling null lists
     *
     * @param list List to remove from (may be null)
     * @param item Item to remove
     * @return Updated list with the item removed
     */
    @NonNull
    public static List<String> safeRemove(@Nullable List<String> list, @Nullable String item) {
        if (list == null || list.isEmpty() || item == null) {
            return list != null ? list : new ArrayList<>();
        }
        
        List<String> result = new ArrayList<>(list);
        result.remove(item);
        return result;
    }
    
    /**
     * Create an unmodifiable view of a List<String>, handling null lists
     *
     * @param list List to make unmodifiable (may be null)
     * @return Unmodifiable view of the list, or an empty unmodifiable list if input was null
     */
    @NonNull
    public static List<String> unmodifiableList(@Nullable List<String> list) {
        return list != null ? Collections.unmodifiableList(list) : Collections.emptyList();
    }
}
