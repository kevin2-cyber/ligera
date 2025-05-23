package com.ligera.app.model.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import java.math.BigDecimal;

/**
 * Room type converter for BigDecimal values
 * <p>
 * This converter allows Room to store BigDecimal values in the database as Strings and
 * convert them back to BigDecimal objects when loading from the database.
 * </p>
 */
public class BigDecimalConverter {
    
    /**
     * Convert a String value from the database to a BigDecimal object
     *
     * @param value String representation of a BigDecimal, or null
     * @return The corresponding BigDecimal object, or null if input was null
     */
    @TypeConverter
    @Nullable
    public static BigDecimal fromString(@Nullable String value) {
        if (value == null) {
            return null;
        }
        
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            // If the string doesn't represent a valid BigDecimal, return BigDecimal.ZERO
            // Alternatively, you could throw an exception, but this approach is more forgiving
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Convert a BigDecimal object to a String for storage in the database
     *
     * @param value BigDecimal object, or null
     * @return String representation of the BigDecimal, or null if input was null
     */
    @TypeConverter
    @Nullable
    public static String toString(@Nullable BigDecimal value) {
        return value == null ? null : value.toString();
    }
}
