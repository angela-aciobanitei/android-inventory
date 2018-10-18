package com.basic.udacity.inventoryapp.ui;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import com.basic.udacity.inventoryapp.R;
import com.basic.udacity.inventoryapp.adapters.InventoryCursorAdapter;
import com.basic.udacity.inventoryapp.database.InventoryContract.InventoryEntry;
import com.basic.udacity.inventoryapp.datamodel.Product;

import java.util.ArrayList;


/**
 * Displays list of products that were entered and stored in the app.
 */
public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    // Identifier for the inventory data loader
    private static final int INVENTORY_LOADER = 0;

    // Adapter for the ListView
    private InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the inventory data
        ListView inventoryListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        inventoryListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of inventory data in the Cursor.
        // There is no inventory data yet (until the loader finishes), pass in null for the Cursor.
        mCursorAdapter = new InventoryCursorAdapter(this, null);
        inventoryListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Start {@link EditorActivity} via an explicit intent
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });


        // Kick off the loader
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);

    }


    /**
     * Helper method to insert hardcoded data into the database. For debugging purposes only.
     */
    private void insertDummyData() {

        ArrayList<Product> products = new ArrayList<>();
        products.add(new Product("Samsung S5", 299.0, 5,
                "Samsung" , "contact@samsungltd.com", "555 555 555",
                BitmapFactory.decodeResource(getResources(), R.raw.samsung_s5)));
        products.add(new Product("Samsung S6", 499.0, 6,
                "Samsung" , "contact@samsungltd.com","555 555 555",
                BitmapFactory.decodeResource(getResources(), R.raw.samsung_s6)));
        products.add(new Product("Samsung S7", 799.0, 7,
                "Samsung" , "contact@samsungltd.com", "555 555 555",
                BitmapFactory.decodeResource(getResources(), R.raw.samsung_s7)));
        products.add(new Product("Samsung S8", 899.0, 8,
                "Samsung" , "contact@samsungltd.com", "555 555 555",
                BitmapFactory.decodeResource(getResources(), R.raw.samsung_s8)));

        for (Product product : products) {
            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_PRODUCT_NAME, product.getName());
            values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, product.getPrice());
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, product.getQuantity());
            values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, product.getSupplierName());
            values.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL, product.getSupplierEmail());
            values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, product.getSupplierPhone());
            values.put(InventoryEntry.COLUMN_PRODUCT_IMAGE, product.getImageInBytes());

            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
        }
    }

    /**
     * Helper method to delete all products in the database.
     */
    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver()
                .delete(InventoryEntry.CONTENT_URI, null, null);
    }

    private void showDeleteAllConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllProducts();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void onSaleClick(long id, int quantity) {
        Uri currentProductUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity - 1);
        int rowsAffected = getContentResolver()
                .update(currentProductUri, values, null, null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_main.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyData();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                showDeleteAllConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_EMAIL,
                InventoryEntry.COLUMN_SUPPLIER_PHONE,
                InventoryEntry.COLUMN_PRODUCT_IMAGE
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(
                this,                    // Parent activity context
                InventoryEntry.CONTENT_URI,     // Provider content URI to query
                projection,                     // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                // No selection arguments
                null);                 // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update {@link InventoryCursorAdapter} with this new cursor containing updated inventory data.
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Prevent memory leaks.
        mCursorAdapter.swapCursor(null);
    }
}

