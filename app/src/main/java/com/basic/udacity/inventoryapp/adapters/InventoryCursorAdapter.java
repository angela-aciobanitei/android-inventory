package com.basic.udacity.inventoryapp.adapters;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.basic.udacity.inventoryapp.R;
import com.basic.udacity.inventoryapp.database.InventoryContract.InventoryEntry;
import com.basic.udacity.inventoryapp.ui.MainActivity;
import com.basic.udacity.inventoryapp.utils.ImageUtils;

/**
 * {@link InventoryCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of inventory data as its data source. This adapter knows
 * how to create list items for each row of inventory data in the {@link Cursor}.
 */

public class InventoryCursorAdapter extends CursorAdapter{

    private MainActivity activity;
    // Constructor matching super
    public InventoryCursorAdapter(MainActivity context, Cursor cursor) {
        super(context, cursor, 0);
        this.activity = context;
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.list_item_name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.list_item_quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.list_item_price);
        ImageView saleImageButton = (ImageView) view.findViewById(R.id.sale_item_image);
        ImageView productImageView = (ImageView) view.findViewById(R.id.list_item_image);

        // Find the columns of product attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_IMAGE);

        // Read the product attributes from the Cursor for the current product
        String name = cursor.getString(nameColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        byte[] blob = cursor.getBlob(imageColumnIndex);


        // Update the views with the attributes for the current product
        nameTextView.setText(name);
        quantityTextView.setText(String.format("Quantity: %s", String.valueOf(quantity)));
        priceTextView.setText(String.format("Price: %s $", String.valueOf(price)));
        if (blob == null) {
            productImageView.setImageResource(R.drawable.default_product);
        } else {
            productImageView.setImageBitmap(ImageUtils.getBitmapFromByteArray(blob));
        }

        //Set an onClickListener for the sale image button
        final long id = cursor.getLong(cursor.getColumnIndex(InventoryEntry._ID));
        final int finalQuantity = quantity;
        saleImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalQuantity > 0) {
                    activity.onSaleClick(id, finalQuantity);
                    Toast.makeText(activity, R.string.successful_sale, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(activity, R.string.product_not_available, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
