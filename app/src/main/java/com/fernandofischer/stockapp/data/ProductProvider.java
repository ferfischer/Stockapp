package com.fernandofischer.stockapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.fernandofischer.stockapp.data.ProductContract.ProductEntry;

/**
 * Created by fernandofischer on 29/08/17.
 */

/**
 * {@link ContentProvider} for Stockapp.
 */
public class ProductProvider extends ContentProvider {

    public static final String LOG_TAG = ProductProvider.class.getSimpleName();
    private ProductDbHelper mDbHelper;

    /** URI matcher code for the content URI for the products table */
    private static final int PRODUCTS = 100;

    /** URI matcher code for the content URI for a single product in the products table */
    private static final int PRODUCT_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,ProductContract.PATH_PRODUCTS,PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,ProductContract.PATH_PRODUCTS + "/#",PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(this.getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor = null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:

                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case PRODUCT_ID:

                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {

        // Checks required fields
        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Product requires a name");
        }

        String supplier = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
        if ( TextUtils.isEmpty(supplier) ) {
            throw new IllegalArgumentException("Product requires a supplier");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Long id = db.insert(ProductEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Checks required fields, if values contains the field
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_SUPPLIER)) {
            String supplier = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            if ( TextUtils.isEmpty(supplier) ) {
                throw new IllegalArgumentException("Product requires a supplier");
            }
        }

        // Se não há valores parar atualizar, então não tenta atualizar o banco de dados
        if (values.size() == 0) {
            return 0;
        }

        // Caso contrário, obtém o banco de dados com permissão de escrita e para atualizar os dados
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Executa a atualização no banco de dados e obtém o número de linhas afetadas
        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        // Se 1 ou mais linhas foram atualizadas, então notifica todos os listeners que os dados na
        // dada URI mudaram
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Retorna o número de registros do banco de dados afetados pelo comando update
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Obtém banco de dados com permissão de escrita
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Identifica o número de registros que foram deletados
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // Deleta todos os registros que correspondem a selection e selection args
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);

                // Se 1 ou mais registros foram deletados, então notifica todos os listeners que os dados do URI mudaram
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsDeleted;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // Deleta uma única linha dada pelo ID na URI
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);

                // Se 1 ou mais registros foram deletados, então notifica todos os listeners que os dados do URI mudaram
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}
