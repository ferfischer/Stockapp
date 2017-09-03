package com.fernandofischer.stockapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.fernandofischer.stockapp.data.ProductContract.ProductEntry;

/**
 * Created by fernandofischer on 29/08/17.
 */

public class ProductDbHelper extends SQLiteOpenHelper {

    final static String DATABASE_NAME = "stock.db";
    final static int DATABASE_VERSION = 1;

    public static final String LOG_TAG = ProductDbHelper.class.getSimpleName();

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PRODUCTS_TABLE =  "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_SUPPLIER + " TEXT NOT NULL);";

        Log.v(LOG_TAG,SQL_CREATE_PRODUCTS_TABLE);

        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
