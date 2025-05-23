package com.ligera.app.db.converter;

import androidx.room.TypeConverter;

import java.math.BigDecimal;

/**
 * Room type converter for BigDecimal
 */
public class BigDecimalConverter {
    @TypeConverter
    public static BigDecimal fromString(String value) {
        return value == null ? null : new BigDecimal(value);
    }

    @TypeConverter
    public static String fromBigDecimal(BigDecimal value) {
        return value == null ? null : value.toString();
    }
}

