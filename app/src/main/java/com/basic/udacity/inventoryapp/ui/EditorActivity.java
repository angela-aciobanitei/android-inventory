package com.basic.udacity.inventoryapp.ui;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import com.basic.udacity.inventoryapp.R;
import com.basic.udacity.inventoryapp.database.InventoryContract.InventoryEntry;
import com.basic.udacity.inventoryapp.utils.ImageUtils;


/**
 * Allows user to create a new product or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    // Identifier for the product data loader
    private static final int EXISTING_INVENTORY_LOADER = 0;

    private static final int PICK_IMAGE_REQUEST = 101 ;

    // Content URI for the existing product (null if it's a new product)
    private Uri mCurrentProductUri;

    // EditText field to enter the products's name
    private EditText mNameEditText;

    // EditText field to enter the products's quantity
    private EditText mQuantityEditText;

    // EditText field to enter the products's price
    private EditText mPriceEditText;

    // EditText field to enter the supplier's name
    private EditText mSupplierEditText;

    // EditText field to enter the supplier's email
    private EditText mEmailEditText;

    // EditText field to enter the supplier's phone
    private EditText mPhoneEditText;

    //ImageView for the product
    private ImageView mProductImageView;

    // Button for adding product image
    Button mAddImageButton;

    // Boolean flag that keeps track of whether the product has been edited (true) or not (false)
    private boolean mProductHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they
     * are modifying the view, and we change the mProductHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity, in order to
        // figure out if we're creating a new product or editing an existing one.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        // If the intent DOES NOT contain a product content URI, then we know that we are
        // creating a new product.
        if (mCurrentProductUri == null) {
            // This is a new product, so change the app bar to say "Add a Product"
            setTitle(getString(R.string.app_bar_title_add_product));

            // Invalidate the options menu, so the "Delete Item" and "Order Item"
            // menu options can be hidden.
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing product, so change app bar to say "Edit Product"
            setTitle(getString(R.string.app_bar_title_edit_product));

            // Initialize a loader to read the inventory data from the database
            // and display the current values in the editor.
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from.
        mNameEditText = findViewById(R.id.edit_product_name);
        mQuantityEditText = findViewById(R.id.edit_product_quantity);
        ImageButton decreaseQuantity = findViewById(R.id.decrease_quantity);
        ImageButton increaseQuantity = findViewById(R.id.increase_quantity);
        mPriceEditText = findViewById(R.id.edit_product_price);
        mSupplierEditText = findViewById(R.id.edit_supplier_name);
        mEmailEditText = findViewById(R.id.edit_supplier_email);
        mPhoneEditText = findViewById(R.id.edit_supplier_phone);
        mProductImageView = findViewById(R.id.product_image);
        mAddImageButton = findViewById(R.id.button_add_image);

        // Set listeners for quantity image buttons
        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String initValString = mQuantityEditText.getText().toString();
                int initVal;
                if (initValString.isEmpty() || initValString.equals("0") ) {
                    return;
                }
                else {
                    initVal = Integer.parseInt(initValString);
                    mQuantityEditText.setText(String.valueOf(initVal - 1));
                }
                mProductHasChanged = true;
            }
        });

        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String initValString = mQuantityEditText.getText().toString();
                int initVal;
                if (initValString.isEmpty()) {
                    initVal = 0;
                } else {
                    initVal = Integer.parseInt(initValString);
                }
                mQuantityEditText.setText(String.valueOf(initVal + 1));
                mProductHasChanged = true;
            }
        });

        // Set listener for "ADD IMAGE" button
        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        decreaseQuantity.setOnTouchListener(mTouchListener);
        increaseQuantity.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mEmailEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);
        mAddImageButton.setOnTouchListener(mTouchListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                mProductImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * Get user input from editor and save product into database.
     */
    private void saveProduct() {
        // Read from input fields. Use trim to eliminate leading or trailing white space.
        String stringName = mNameEditText.getText().toString().trim();
        String stringPrice = mPriceEditText.getText().toString().trim();
        String stringQuantity = mQuantityEditText.getText().toString().trim();
        String stringSupplierName = mSupplierEditText.getText().toString().trim();
        String stringEmail = mEmailEditText.getText().toString().trim();
        String stringPhone = mPhoneEditText.getText().toString().trim();
        Bitmap imageBitmap = null;
        if (mProductImageView.getDrawable() != null) {
            imageBitmap = ((BitmapDrawable) mProductImageView.getDrawable()).getBitmap();
        }

        // Check if this is supposed to be a new product
        // and check if all the fields in the editor are blank.
        if (mCurrentProductUri == null
                && TextUtils.isEmpty(stringName)  && TextUtils.isEmpty(stringPrice)
                && TextUtils.isEmpty(stringQuantity) && TextUtils.isEmpty(stringSupplierName)
                && TextUtils.isEmpty(stringEmail) && TextUtils.isEmpty(stringPhone)) {
            Toast.makeText(this, "Please insert information first.",Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if product name, product price, product quantity,
        // supplier name, supplier email, supplier phone fields in the editor are blank.
        boolean isProductValid = true;
        String message = "";

        if ( TextUtils.isEmpty(stringName)) {
            message = message + getString(R.string.name_tag);
            isProductValid = false;
        }

        if (TextUtils.isEmpty(stringPrice)) {
            if (!isProductValid) {message = message + ", ";}
            message = message + getString(R.string.price_tag);
            isProductValid = false;
        }

        if (TextUtils.isEmpty(stringQuantity)) {
            if (!isProductValid) {message = message + ", ";}
            message = message + getString(R.string.quantity_tag);
            isProductValid = false;
        }

        if (TextUtils.isEmpty(stringSupplierName)) {
            if (!isProductValid) {message = message + ", ";}
            message = message + getString(R.string.supplier_tag);
            isProductValid = false;
        }

        if (TextUtils.isEmpty(stringEmail)) {
            if (!isProductValid) {message = message + ", ";}
            message = message + getString(R.string.email_tag);
            isProductValid = false;
        }

        if (TextUtils.isEmpty(stringPhone)) {
            if (!isProductValid) {message = message + ", ";}
            message = message + getString(R.string.phone_tag);
            isProductValid = false;
        }

        if (!isProductValid) {
            Toast.makeText(this, "Invalid field(s): " + message + ".",Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, stringName);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, stringPrice);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, stringQuantity);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, stringSupplierName);
        values.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL, stringEmail);
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE,stringPhone);
        values.put(InventoryEntry.COLUMN_PRODUCT_IMAGE,
                ImageUtils.getByteArrayFromBitmap(imageBitmap));

        // Determine if this is a new or existing product by checking if URI is null or not.
        if (mCurrentProductUri == null) {
            // This is a NEW product, so INSERT a new product into the provider,
            // returning the content URI for the new product.
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Error when saving product.", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Product saved.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING product, so UPDATE the product with content URI
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because current URI will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver()
                    .update(mCurrentProductUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Error when updating product.",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Product updated.",
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete"  and "Order" menu items.
        if (mCurrentProductUri == null) {
            MenuItem menuItemDelete = menu.findItem(R.id.action_delete_item);
            MenuItem menuItemOrder = menu.findItem(R.id.action_order);
            menuItemDelete.setVisible(false);
            menuItemOrder.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu.
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option.
            case R.id.action_save:
                saveProduct();
                return true;
            // Respond to a click on the "Delete" menu option.
            case R.id.action_delete_item:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Order" menu option.
            case R.id.action_order:
                showOrderConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar.
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up
                // to parent activity which is the {@link MainActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that changes
                // should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes.
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press.
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes.
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog( DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_message);
        builder.setPositiveButton(R.string.unsaved_changes_dialog_pos_btn, discardButtonClickListener);
        builder.setNegativeButton(R.string.unsaved_changes_dialog_neg_btn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Dismiss the dialog and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog.
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_confirmation_dialog_message);
        builder.setPositiveButton(R.string.delete_confirmation_dialog_pos_btn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.delete_confirmation_dialog_neg_btn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (mCurrentProductUri != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentProductUri
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver()
                    .delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "Error when deleting product.",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "Product deleted.",
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    private void showOrderConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.order_confirmation_dialog_message);
        builder.setPositiveButton(R.string.order_confirmation_dialog_pos_btn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Intent to email
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:" + mEmailEditText.getText().toString().trim()));
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_intent_extra_subject));
                String bodyMessage = getString(R.string.email_intent_extra_message) +
                        mNameEditText.getText().toString().trim();
                intent.putExtra(Intent.EXTRA_TEXT, bodyMessage);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.order_confirmation_dialog_neg_btn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Intent to phone
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mPhoneEditText.getText().toString().trim()));
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all product attributes, define a projection
        // that contains all columns from the inventory table.
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

        // This loader will execute the ContentProvider's query method on a background thread.
        return new CursorLoader(
                this,               // Parent activity context
                mCurrentProductUri,        // Query the content URI for the current product
                projection,                // Columns to include in the resulting Cursor
                null,              // No selection clause
                null,           // No selection arguments
                null);            // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor.
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in.
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int emailColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_EMAIL);
            int phoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE);
            int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_IMAGE);

            // Extract out the value from the Cursor for the given column index.
            String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String email = cursor.getString(emailColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);
            byte[] blob = cursor.getBlob(imageColumnIndex);

            // Update the views on the screen with the values from the database.
            mNameEditText.setText(name);
            mQuantityEditText.setText(String.valueOf(quantity));
            mPriceEditText.setText(String.valueOf(price));
            mSupplierEditText.setText(supplier);
            mEmailEditText.setText(email);
            mPhoneEditText.setText(phone);
            if (blob == null) {
                mProductImageView.setImageResource(R.drawable.default_product);
            } else {
                mProductImageView.setImageBitmap(ImageUtils.getBitmapFromByteArray(blob));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mSupplierEditText.setText("");
        mEmailEditText.setText("");
        mPhoneEditText.setText("");
        mProductImageView.setImageResource(R.drawable.default_product);
    }
}

