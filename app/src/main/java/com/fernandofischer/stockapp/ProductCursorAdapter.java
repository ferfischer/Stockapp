package com.fernandofischer.stockapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.fernandofischer.stockapp.data.ProductContract.ProductEntry;

/**
 * Created by fernandofischer on 30/08/17.
 */

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /*flags*/);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if (cursor != null) {
            TextView nameTextView = (TextView) view.findViewById(R.id.name);
            TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
            TextView priceTextView = (TextView) view.findViewById(R.id.price);

            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);

            // Read the products attributes from the Cursor for the current product
            String productName = cursor.getString(nameColumnIndex);
            String productQuantity = cursor.getString(quantityColumnIndex);
            String productPrice = Utils.priceToString(cursor.getInt(priceColumnIndex));

            // Update the TextViews with the attributes for the current product
            nameTextView.setText(productName);
            quantityTextView.setText(productQuantity + " " + view.getResources().getString(R.string.items_in_stock));
            priceTextView.setText(productPrice);
        }
    }
}
