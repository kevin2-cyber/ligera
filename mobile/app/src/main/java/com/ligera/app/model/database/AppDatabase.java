package com.ligera.app.model.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.ligera.app.model.converter.BigDecimalConverter;
import com.ligera.app.model.converter.StringListConverter;
import com.ligera.app.model.dao.CategoryDao;
import com.ligera.app.model.dao.ProductDao;
import com.ligera.app.model.entity.Category;
import com.ligera.app.model.entity.Product;
import com.ligera.app.util.AppExecutors;
import com.ligera.app.view.util.Constants;

import java.util.concurrent.Executor;

/**
 * Main database class for the application
 */
@Database(
    entities = {
        Product.class,
        Category.class
    },
    version = 1,
    exportSchema = false
)
@TypeConverters({
    StringListConverter.class,
    BigDecimalConverter.class
})
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String TAG = "AppDatabase";
    private static final String DATABASE_NAME = "ligera_db";
    
    private static volatile AppDatabase INSTANCE;
    
    public abstract ProductDao productDao();
    public abstract CategoryDao categoryDao();
    
    /**
     * Get database instance using singleton pattern
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
                            .addCallback(roomCallback)
                            .fallbackToDestructiveMigration() // Use with caution in production
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    
    /**
     * Room database callback for initialization
     */
    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Initialize database with sample data on first creation
            initializeData();
        }
        
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            // Operations to perform every time the database is opened
            Log.d(TAG, "Database opened");
        }
    };
    
    /**
     * Initialize database with sample data
     */
    private static void initializeData() {
        Executor databaseWriteExecutor = new AppExecutors().diskIO();
        databaseWriteExecutor.execute(() -> {
            try {
                // Get DAOs
                ProductDao productDao = INSTANCE.productDao();
                CategoryDao categoryDao = INSTANCE.categoryDao();
                
                // Only populate if database is empty
                if (productDao.countProducts() == 0 && categoryDao.countCategories() == 0) {
                    Log.d(TAG, "Initializing database with sample data");
                    
                    // Create sample categories and insert them
                    Category categoryOne = new Category();
                    categoryOne.setName("Ligera Collection");
                    categoryOne.setDescription("Our flagship collection");
                    categoryDao.insert(categoryOne);

                    Category categoryTwo = new Category();
                    categoryTwo.setName("Amor Collection");
                    categoryTwo.setDescription("Romantic styles for all occasions");
                    categoryDao.insert(categoryTwo);

                    Log.d(TAG, "Database initialized with categories");

                    // Insert sample products
                    productDao.insertAll(Constants.getProductData());
                    Log.d(TAG, "Database initialized with sample products");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error initializing database", e);
            }
        });
    }
}
