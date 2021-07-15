package sigma.gaming.storeinventory;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import sigma.gaming.storeinventory.data.ProductContract.ProductEntry;

/**
 * Allows user to create a new product or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {


    /**
     * Identifier for the product data loader
     */
    private static final int EXISTING_PRODUCT_LOADER = 0;

    /**
     * URI of product image
     */
    private Uri mImageUri;

    /**
     * Current quantity of product
     */
    private int mQuantity;

    /**
     * Content URI for the existing product (null if it's a new product)
     */
    private Uri mCurrentProductUri;

    /**
     * EditText field to enter the product's name
     */
    private EditText mNameEditText;


    /**
     * EditText field to enter the product's model
     */
    private EditText mModelEditText;

    /**
     * EditText field to enter the product's grade
     */
    private Spinner mGradeSpinner;

    /**
     * ImageView field to enter the product's photo
     */
    private ImageView mPhoto;

    /**
     * EditText field to enter the product's price
     */
    private EditText mPrice;

    /**
     * EditText field to enter the supplier's namee
     */
    private EditText mSupplierName;

    /**
     * EditText field to enter the supplier's e-mail
     */
    private EditText mSupplierEmail;

    /**
     * EditText field to enter the product's quantity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to displaying hint on EditorActivity at the bottom of photo
     */
    private TextView mPhotoHintText;

    /**
     * Button ADD PRODUCT to increasing amount of product in the storehouse
     */
    private Button mAddProductButton;

    /**
     * Button REJECT PRODUCT to decreasing amount of product in the storehouse
     */
    private Button mRejectProductButton;


    /**
     * Grade of the product. The possible valid values are in the ProductContract.java file:
     * {@link ProductEntry#GRADE_UNKNOWN}, {@link ProductEntry#GRADE_NEW}, or
     * {@link ProductEntry#GRADE_USED}.
     */
    private int mGrade = ProductEntry.GRADE_UNKNOWN;

    /**
     * Boolean flag that keeps track of whether the product has been edited (true) or not (false)
     */
    private boolean mProductHasChanged = false;

    /**
     * EditText field to enter the product's quantity
     */
    private int mCurrentQuantity = 0;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mProductHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    // BOOLEAN status for required fields,TRUE if these fields have been populated
    boolean hasAllRequiredValues = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new product or editing an existing one.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mModelEditText = (EditText) findViewById(R.id.edit_product_model);
        mQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        mGradeSpinner = (Spinner) findViewById(R.id.spinner_grade);
        mPhoto = (ImageView) findViewById(R.id.edit_product_photo);
        mPrice = (EditText) findViewById(R.id.edit_product_price);
        mSupplierName = (EditText) findViewById(R.id.edit_product_supplier_name);
        mSupplierEmail = (EditText) findViewById(R.id.edit_product_supplier_email);
        mAddProductButton = (Button) findViewById(R.id.addProductButton);
        mRejectProductButton = (Button) findViewById(R.id.rejectProductButton);
        mPhotoHintText = (TextView) findViewById(R.id.add_or_edit_photo_hint);


        // If the intent DOES NOT contain a product content URI, then we know that we are
        // creating a new product.
        if (mCurrentProductUri == null) {
            // This is a new product, so change the app bar to say "Add a Product"
            setTitle(getString(R.string.editor_activity_title_new_product));
            mPhotoHintText.setText(getText(R.string.add_photo_hint_text));
            mSupplierName.setEnabled(true);
            mSupplierEmail.setEnabled(true);
            mQuantityEditText.setEnabled(true);
            mPhoto.setImageResource(R.drawable.ic_empty_storehouse);
            mAddProductButton.setVisibility(View.GONE);
            mRejectProductButton.setVisibility(View.GONE);

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a product that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing product, so change app bar to say "Edit Product"
            setTitle(getString(R.string.editor_activity_title_edit_product));
            mPhotoHintText.setText(getText(R.string.edit_photo_hint_text));
            mSupplierName.setEnabled(false);
            mSupplierEmail.setEnabled(false);
            mQuantityEditText.setEnabled(false);
            mAddProductButton.setVisibility(View.VISIBLE);
            mRejectProductButton.setVisibility(View.VISIBLE);

            // Initialize a loader to read the product data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mModelEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mGradeSpinner.setOnTouchListener(mTouchListener);
        mPrice.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mSupplierEmail.setOnTouchListener(mTouchListener);
        mAddProductButton.setOnTouchListener(mTouchListener);
        mRejectProductButton.setOnTouchListener(mTouchListener);

        mAddProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemButton(v);
            }
        });

        mRejectProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectItemButton(v);
            }
        });

        /**
         * Here we are setting the image for our product in the editor page
         * when we click on the choose image
         * this listener will trigger so we can select our image
         */
        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySelector();
                mProductHasChanged = true;
            }
        });

        setupSpinner();


    }

    /**
     * this trySelector method we created is mainly for checking the permissions
     * we require for getttng the image from device
     */

    public void trySelector() {
        /**
         * To check if the user has already granted your app a particular permission,
         * pass that permission into the ContextCompat.checkSelfPermission() method.
         * This method returns either PERMISSION_GRANTED or PERMISSION_DENIED,
         * depending on whether your app has the permission.
         *
         * and the  Manifest.permission.READ_EXTERNAL_STORAGE give the permission to read the storage
         *
         *  PackageManager provides methods for querying and manipulating installed packages
         *  and related permissions, etc.
         */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }
        openSelector();
    }


    /**
     * this method is  created so that we can send the intent and get to the locations
     * where the images are and select the image
     */

    private void openSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);

            /**
             ACTION_GET_CONTENT Allow the user to select a particular kind of
             data and return it.This is different than ACTION_PICK in that here
             we just say what kind of data is desired, not a URI of existing data
             from which the user can pick. An ACTION_GET_CONTENT could allow the
             user to create the data as it runs (for example taking a picture or
             recording a sound), let them browse over the web and download
             the desired data, etc.
             */
        } else {

            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            /**
             Allow the user to select and return one or more existing documents.
             When invoked, the system will display the various DocumentsProvider
             instances installed on the device, letting the user interactively
             navigate through them. These documents include local media, such
             as photos and video, and documents provided by installed cloud storage providers.
             */

            intent.addCategory(Intent.CATEGORY_OPENABLE);
            /**
             Intent.CATEGORY is another specification you can add to your intent. In your case
             OPENABLE denotes that the data you want to recieve can be opened as a File object
             i.e. with read and write permissions and have complete access to the physical location of
             the data(in your case the image file). Remmember that this means you will need to request
             the storage-permissions before hand and this category will cause a raise in your securities.
             If you just need to display the image and do not need to manipulate the image file physically,
             DON'T USE this category. If you want to manipulate the image and store it SEPARATELY consider
             making a separate copy of the image and manipulate that copy in your own ExternalStorage provided by Android.
             */
        }
        intent.setType(getString(R.string.intent_type));

        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), 0);
    }

    /**
     * @param requestCode
     * @param permissions
     * @param grantResults here this method is use for the permission that we will ask when we click on "Tap to add a photo" .
     *                     this will ask the permission to access the storage for the image that we need
     *                     in this the switch(requestcode)  it  allow us  to check if the user gives the permission that were ask to access
     *                     the storage or not  if the user gives the permission then we will call the openSelector() method
     *                     to send  the intent and get to the locations where the images are and select the image.
     */


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openSelector();
                }
        }
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data        this method is create so that it will Called when an activity you launched exits, giving you the
     *                    requestCode you started it with,the resultCode it returned, and any additional data from it.
     *                    The resultCode will be RESULT_CANCELED if the activity explicitly returned that, didn't
     *                    return any result, or crashed during its operation.
     *                    this will set the imageView with the imageUri that we created and  set the image as data that
     *                    we get from the above methods .
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        mImageUri = data.getData();
                        mPhoto.setImageURI(mImageUri);
                        mPhoto.invalidate();
                    }
                }
                break;
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        mImageUri = data.getData();
                        mPhoto.setImageURI(mImageUri);
                        mPhoto.invalidate();
                    }
                    break;


                }
        }

    }


    public void orderMore() {
        Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:" + mSupplierEmail.getText().toString().trim()));
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New order: " +
                mNameEditText.getText().toString().trim() +
                " " + mModelEditText.getText().toString().trim());
        String message = "We need to make a new order of: " +
                mNameEditText.getText().toString().trim() +
                " " +
                mModelEditText.getText().toString().trim() + "." +
                "\n" +
                "Please confirm that you can send to us ___ pcs." +
                "\n" +
                "\n" +
                "Best regards," + "\n" +
                "_________________";
        intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
        startActivity(intent);
    }


    /**
     * Setup the dropdown spinner that allows the user to select the grade of the product.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter gradeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_grade_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        gradeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGradeSpinner.setAdapter(gradeSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.grade_new))) {
                        mGrade = ProductEntry.GRADE_NEW;
                    } else if (selection.equals(getString(R.string.grade_used))) {
                        mGrade = ProductEntry.GRADE_USED;
                    } else {
                        mGrade = ProductEntry.GRADE_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGrade = ProductEntry.GRADE_UNKNOWN;
            }
        });
    }

    /**
     * Get user input from editor and save product into database.
     */
    private boolean saveProduct() {


        // Quantity of products
        int quantity;

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String modelString = mModelEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPrice.getText().toString().trim();
        String supplierNameString = mSupplierName.getText().toString().trim();
        String supplierEmailString = mSupplierEmail.getText().toString().trim();


        // Check if this is supposed to be a new product
        // and check if all the fields in the editor are blank
        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(modelString) &&
                TextUtils.isEmpty(quantityString) &&
                mGrade == ProductEntry.GRADE_UNKNOWN &&
                TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierEmailString) &&
                mImageUri == null) {
            // Since no fields were modified, we can return early without creating a new product.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            hasAllRequiredValues = true;
            return hasAllRequiredValues;
        }

        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        ContentValues values = new ContentValues();

        // REQUIRED VALUES
        // Validation section
        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getString(R.string.validation_msg_product_name), Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        }

        if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, getString(R.string.validation_msg_product_quantity), Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            // If the quantity is not provided by the user, don't try to parse the string into an
            // integer value. Use 0 by default.
            quantity = Integer.parseInt(quantityString);
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        }

        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, getString(R.string.validation_msg_product_price), Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceString);
        }

        if (mImageUri == null) {
            Toast.makeText(this, getString(R.string.validation_msg_product_image), Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(ProductEntry.COLUMN_PRODUCT_PICTURE, mImageUri.toString());
        }

        // OPTIONAL VALUES
        values.put(ProductEntry.COLUMN_PRODUCT_MODEL, modelString);             // optional, nullable
        values.put(ProductEntry.COLUMN_PRODUCT_GRADE, mGrade);                  // always have a value
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierNameString);      // optional, nullable
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, supplierEmailString);    // optional, nullable


        // Determine if this is a new or existing product by checking if mCurrentProductUri is null or not
        if (mCurrentProductUri == null) {
            // This is a NEW product, so insert a new product into the provider,
            // returning the content URI for the new product.
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING product, so update the product with content URI: mCurrentProductUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentProductUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }

        }

        hasAllRequiredValues = true;
        return hasAllRequiredValues;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                saveProduct();
                if (hasAllRequiredValues == true) {
                    // Exit activity
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Order more" menu option
            case R.id.action_order_more:
                orderMore();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
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

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all product attributes, define a projection that contains
        // all columns from the product table
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_MODEL,
                ProductEntry.COLUMN_PRODUCT_GRADE,
                ProductEntry.COLUMN_PRODUCT_PICTURE,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_EMAIL,
                ProductEntry.COLUMN_PRODUCT_QUANTITY
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,       // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current product
                projection,                 // Columns to include in the resulting Cursor
                null,                       // No selection clause
                null,                       // No selection arguments
                null);                      // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int modelColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_MODEL);
            int gradeColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_GRADE);
            int pictureColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PICTURE);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierEmailColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_EMAIL);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String model = cursor.getString(modelColumnIndex);
            int grade = cursor.getInt(gradeColumnIndex);
            String imageUriString = cursor.getString(pictureColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierEmail = cursor.getString(supplierEmailColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            mQuantity = quantity;
            mImageUri = Uri.parse(imageUriString);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mModelEditText.setText(model);
            mPhoto.setImageURI(mImageUri);
            mPrice.setText(price);
            mSupplierName.setText(supplierName);
            mSupplierEmail.setText(supplierEmail);
            mQuantityEditText.setText(Integer.toString(quantity));

            // Grade is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (grade) {
                case ProductEntry.GRADE_NEW:
                    mGradeSpinner.setSelection(1);
                    break;
                case ProductEntry.GRADE_USED:
                    mGradeSpinner.setSelection(2);
                    break;
                default:
                    mGradeSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mModelEditText.setText("");
        mPhoto.setImageResource(R.drawable.ic_empty_storehouse);
        mPrice.setText("");
        mSupplierName.setText("");
        mSupplierEmail.setText("");
        mQuantityEditText.setText("");
        mGradeSpinner.setSelection(0); // Select "Unknown" grade
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this product.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (mCurrentProductUri != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentProductUri
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    public void addItemButton(View view) {
        mQuantity++;
        displayQuantity();
    }

    public void rejectItemButton(View view) {
        if (mQuantity == 0) {
            Toast.makeText(this, "Can't decrease quantity", Toast.LENGTH_SHORT).show();
        } else {
            mQuantity--;
            displayQuantity();
        }
    }

    public void displayQuantity() {
        mQuantityEditText.setText(String.valueOf(mQuantity));
    }
}
