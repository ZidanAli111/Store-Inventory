package sigma.gaming.storeinventory;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import sigma.gaming.storeinventory.data.ProductContract.ProductEntry;

public class EditorActivity extends AppCompatActivity {


    boolean hasAllRequiredValues = false;
    private EditText mProductNameEditText;
    private EditText mModelNameEditText;
    private EditText mProductPriceEditText;
    private Uri mImageUri;
    private ImageView mPhotoImageView;
    private TextView mPhotoHintTextView;
    private Spinner mGradeSpinner;
    private EditText mSupplierNameEditText;
    private EditText mSupplierEmailEditText;
    private EditText mQuantityEditText;
    private Button mAddProductButton;
    private Button mRemoveProductButton;
    private int mGrade = ProductEntry.GRADE_UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mProductNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mModelNameEditText = (EditText) findViewById(R.id.edit_product_model);
        mProductPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mPhotoImageView = (ImageView) findViewById(R.id.product_image);
        mPhotoHintTextView = (TextView) findViewById(R.id.add_or_edit_photo_hint);
        mGradeSpinner = (Spinner) findViewById(R.id.spinner_grade);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_product_supplier_name);
        mSupplierEmailEditText = (EditText) findViewById(R.id.edit_product_supplier_email);
        mQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        mAddProductButton = (Button) findViewById(R.id.addProductButton);
        mRemoveProductButton = (Button) findViewById(R.id.rejectProductButton);


    }


    private void setupSpinner() {

        ArrayAdapter gradeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_grade_options, android.R.layout.simple_dropdown_item_1line);
        gradeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mGradeSpinner.setAdapter(gradeSpinnerAdapter);
    }

    private boolean savePet() {

        int quantity;
        String nameString = mProductNameEditText.getText().toString().trim();
        String modelString = mModelNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mProductPriceEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierEmailString = mSupplierEmailEditText.getText().toString().trim();

        ContentValues contentValues = new ContentValues();
        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, "Product name is Required", Toast.LENGTH_SHORT).show();

            return hasAllRequiredValues;
        } else {
            contentValues.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        }
        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, "Quantity is Required", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        }
        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, "Price is Required", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            contentValues.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceString);

        }
        if (mImageUri == null) {
            Toast.makeText(this, "Photo of the Product is Required", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            contentValues.put(ProductEntry.COLUMN_PRODUCT_PICTURE, mImageUri.toString());
        }


        contentValues.put(ProductEntry.COLUMN_PRODUCT_MODEL, modelString);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_GRADE, mGrade);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierNameString);
        contentValues.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, supplierEmailString);
        return hasAllRequiredValues;
    }

}
