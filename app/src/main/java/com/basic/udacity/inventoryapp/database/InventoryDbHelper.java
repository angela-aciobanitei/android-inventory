package com.basic.udacity.inventoryapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.basic.udacity.inventoryapp.database.InventoryContract.InventoryEntry;

/**
 * Database helper for Inventory app.
 * Manages database creation and version management.
 */
public class InventoryDbHelper extends SQLiteOpenHelper {

    // Database name and version
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    // Constructor calling super
    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the inventory table
        String SQL_CREATE_INVENTORY_TABLE ="CREATE TABLE " +
                InventoryEntry.TABLE_NAME + "(" +
                InventoryContract.InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                InventoryEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL," +
                InventoryEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL," +
                InventoryEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL," +
                InventoryEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL," +
                InventoryEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL," +
                InventoryEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL," +
                InventoryEntry.COLUMN_PRODUCT_IMAGE + " BLOB " + ");";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}

