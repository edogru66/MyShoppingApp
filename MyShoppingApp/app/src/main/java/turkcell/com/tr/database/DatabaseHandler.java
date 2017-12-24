package turkcell.com.tr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import turkcell.com.tr.myshoppingapp.R;
import turkcell.com.tr.pojo.Product;

/**
 * Created by emre on 24.12.2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    public final String TAG = this.getClass().getName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "shopping-db";
    private static final String TABLE_PRODUCTS = "products";

    private static final String KEY_PRODUCT_ID = "product_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PRICE = "price";
    private static final String KEY_IMAGE = "imageUrl";
    private static final String KEY_DESCRIPTION = "description";



    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_PRODUCTS + "(" +
                KEY_PRODUCT_ID + " TEXT PRIMARY KEY," +
                KEY_NAME + " TEXT NOT NULL," +
                KEY_PRICE + " INTEGER NOT NULL," +
                KEY_IMAGE + " TEXT NOT NULL," +
                KEY_DESCRIPTION + " TEXT)";
        Log.e(TAG, "onCreate: " + createTable);
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    public void addProduct(Product p) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_PRODUCT_ID, p.getId());
        contentValues.put(KEY_NAME, p.getName());
        contentValues.put(KEY_PRICE, p.getPrice());
        contentValues.put(KEY_IMAGE, p.getImageUrl());
        contentValues.put(KEY_DESCRIPTION, p.getDescription());

        SQLiteDatabase db = getWritableDatabase();
        db.insertWithOnConflict(TABLE_PRODUCTS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void addProducts(List<Product> listOfProducts) {
        for (Product p:listOfProducts) {
            addProduct(p);
        }
    }

    public List<Product> getAllProducts() {
        List<Product> result = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_PRODUCTS;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            do {
                Product p = new Product(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                        cursor.getInt(cursor.getColumnIndex(KEY_PRICE)),
                        cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                result.add(p);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return result;
    }

    public Product getProductById(String id) {
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + KEY_PRODUCT_ID + "='" + id + "'";
        SQLiteDatabase db = getReadableDatabase();
        Product p = null;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.getCount() > 0 ) {
                cursor.moveToFirst();
                p = new Product(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                        cursor.getInt(cursor.getColumnIndex(KEY_PRICE)),
                        cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
            }
            cursor.close();
        }
        return p;


    }
}
