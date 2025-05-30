package com.ligera.app.model.database;

import android.content.Context;

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
import com.ligera.app.view.util.Constants;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

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
    
    private static final String DATABASE_NAME = "ligera_db";
    
    private static volatile AppDatabase INSTANCE;
    
    private static final int NUMBER_OF_THREADS = 4;
    
    /**
     * Database write executor for background operations
     */
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    
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
     * Abstract method for Product DAO
     *
     * @return ProductDao
     */
    public abstract ProductDao productDao();
    
    /**
     * Abstract method for Category DAO
     *
     * @return CategoryDao
     */
    public abstract CategoryDao categoryDao();
    
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
            Timber.d("Database opened");
        }
    };
    
    /**
     * Initialize database with sample data
     */
    private static void initializeData() {
        // Execute on background thread to avoid blocking the UI
        databaseWriteExecutor.execute(() -> {
            try {
                // Get DAOs
                ProductDao productDao = INSTANCE.productDao();
                CategoryDao categoryDao = INSTANCE.categoryDao();
                
                // Only populate if database is empty
                if (categoryDao.countCategories() == 0) {
                    Timber.d("Initializing database with sample data");
                    
                    // Create sample categories
                    Category categoryOne = new Category();
                    categoryOne.setName("Ligera Collection");
                    categoryOne.setDescription("Our flagship collection");
                    
                    Category categoryTwo = new Category();
                    categoryTwo.setName("Amor Collection");
                    categoryTwo.setDescription("Romantic styles for all occasions");
                    
                    // Insert categories
                    long categoryOneId = categoryDao.insert(categoryOne);
                    long categoryTwoId = categoryDao.insert(categoryTwo);
                    
                    // Load sample products
                    List<Product> products = Constants.getProductData();
                    
                    // If sample products are available, insert them
                    if (products != null && !products.isEmpty()) {
                        productDao.insertAll(products);
                    }
                    
                    // Update category product counts
                    int categoryOneCount = productDao.countProductsByCategory(categoryOneId);
                    int categoryTwoCount = productDao.countProductsByCategory(categoryTwoId);
                    
                    categoryDao.updateProductCount(categoryOneId, categoryOneCount);
                    categoryDao.updateProductCount(categoryTwoId, categoryTwoCount);
                    
                    Timber.d("Database initialized with %d categories and %d products", 
                            categoryDao.countCategories(), productDao.countProducts());
                }
            } catch (Exception e) {
                Timber.e(e, "Error initializing database");
            }
        });
    }
    
    /**
     * Clear all data from the database
     */
    public void clearAllData() {
        databaseWriteExecutor.execute(() -> {
            try {
                // Delete all data in specific order to handle foreign key constraints
                productDao().deleteAll();
                categoryDao().deleteAll();
                Timber.d("All database data cleared");
            } catch (Exception e) {
                Timber.e(e, "Error clearing database");
            }
        });
    }
    
    /**
     * Reset the singleton instance (for testing purposes)
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }
    
    /**
     * Run database operations asynchronously
     *
     * @param runnable The runnable to execute
     */
    public static void runAsync(Runnable runnable) {
        databaseWriteExecutor.execute(runnable);
    }
    
    /**
     * Get database version
     *
     * @return database version
     */
    public static int getVersion() {
        return 1;
    }
}
