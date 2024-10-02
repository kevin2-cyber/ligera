package com.ligera.app.model.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.ligera.app.model.dao.CategoryDao;
import com.ligera.app.model.dao.ProductDao;
import com.ligera.app.model.entity.Category;
import com.ligera.app.model.entity.Product;
import com.ligera.app.view.util.Constants;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {Category.class, Product.class}, version = 1, exportSchema = false)
public abstract class ProductDatabase extends RoomDatabase {

    public abstract CategoryDao categoryDao();
    public abstract ProductDao productDao();

    // Singleton
    private static ProductDatabase instance;
    public static synchronized ProductDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    ProductDatabase.class,
                    "product_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    // callback
    private static final Callback roomCallback = new Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // insert data when database is created
            initializeData();
        }
    };

    private static void initializeData() {
        ProductDao productDao = instance.productDao();
        CategoryDao categoryDao = instance.categoryDao();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // categories
            Category categoryOne = new Category();
            categoryOne.setName("Ligera Collection");

            Category categoryTwo = new Category();
            categoryTwo.setName("Amor Collection");

            categoryDao.insert(categoryOne);
            categoryDao.insert(categoryTwo);

            // products
            List<Product> products = Constants.getProductData();
            productDao.insert(products.get(0));
            productDao.insert(products.get(1));
            productDao.insert(products.get(2));
            productDao.insert(products.get(3));
            productDao.insert(products.get(4));
            productDao.insert(products.get(5));
            productDao.insert(products.get(6));
            productDao.insert(products.get(7));
            productDao.insert(products.get(8));
        });
    }
}
