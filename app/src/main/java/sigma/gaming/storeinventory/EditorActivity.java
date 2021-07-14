package sigma.gaming.storeinventory;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EditorActivity extends AppCompatActivity{


    private EditText mProductNameEditText;

    private  EditText mModelNameEditText;

    private  EditText mProductPriceEditText;

    private Uri mImageUri;

    private ImageView mPhotoImageView;

    private TextView mPhotoHintTextView;

    private Spinner mGradeSpinner;

    private EditText mSupplierNameEditText;

    private  EditText mSupplierEmailEditText;

    private  EditText mQuantityEditText;

    private Button mAddProductButton;

    private Button mRemoveProductButton;



    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mProductNameEditText=(EditText)findViewById(R.id.edit_product_name);
        mModelNameEditText=(EditText) findViewById(R.id.edit_product_model);
        mProductPriceEditText=(EditText) findViewById(R.id.edit_product_price);
        mPhotoImageView= (ImageView) findViewById(R.id.product_image);
        mPhotoHintTextView=(TextView)findViewById(R.id.add_or_edit_photo_hint);
        mGradeSpinner=(Spinner)findViewById(R.id.spinner_grade);
        mSupplierNameEditText=(EditText)findViewById(R.id.edit_product_supplier_name);
        mSupplierEmailEditText=(EditText)findViewById(R.id.edit_product_supplier_email);
        mQuantityEditText=(EditText)findViewById(R.id.edit_product_quantity);
        mAddProductButton=(Button)findViewById(R.id.addProductButton);
        mRemoveProductButton=(Button)findViewById(R.id.rejectProductButton);


    }


    private  void setupSpinner(){

        ArrayAdapter gradeSpinnerAdapter=ArrayAdapter.createFromResource(this,
                R.array.array_grade_options, android.R.layout.simple_dropdown_item_1line);
        gradeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mGradeSpinner.setAdapter(gradeSpinnerAdapter);
    }
}
