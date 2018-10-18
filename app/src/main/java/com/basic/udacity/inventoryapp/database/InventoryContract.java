package com.basic.udacity.inventoryapp.database;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {

    // The empty constructor prevents someone from accidentally instantiating the contract class.
    private InventoryContract() {

    }

    public static final String CONTENT_AUTHORITY = "com.basic.udacity.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY = "inventory";

    /**
     * Inner class that defines constant values for the inventory database table.
     * Each entry in the table represents a single product.
     */
    public static final class InventoryEntry implements BaseColumns {

        // The content URI to access the inventory data in the provider
        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        // The MIME type of the CONTENT_URI for a list of products.
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        // The MIME type of the CONTENT_URI for a single product.
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public final static String TABLE_NAME = "inventory";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME ="name";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";
        public final static String COLUMN_SUPPLIER_EMAIL = "supplier_email";
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";
        public static final String COLUMN_PRODUCT_IMAGE = "image";
    }
}

