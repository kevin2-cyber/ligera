package com.ligera.app.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.ligera.app.db.converter.BigDecimalConverter;
import com.ligera.app.db.converter.StringListConverter;
import com.ligera.app.db.dao.CategoryDao;
import com.ligera.app.db.dao.ProductDao;
import com.ligera.app.db.entity.CategoryEntity;
import com.ligera.app.db.entity.ProductEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main database class for the application
 */
@Database(
    entities = {
        ProductEntity.class,
        CategoryEntity.class
    },
    version = 1,
    exportSchema = true
)
@TypeConverters({
    StringListConverter.class,
    BigDecimalConverter.class
})
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "ligera_db";
    
    private static volatile AppDatabase INSTANCE;
    
    private static final int NUMBER_OF_THREADS = 4;
    
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    
    /**
     * Get database instance
     *
     * @param context Application context
     * @return Database instance
     */
    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME)
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    // Pre-populate database here if needed
                                }
                            })
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    
    /**
     * Get product DAO
     *
     * @return ProductDao
     */
    public abstract ProductDao productDao();
    
    /**
     * Get category DAO
     *
     * @return CategoryDao
     */
    public abstract CategoryDao categoryDao();
}

