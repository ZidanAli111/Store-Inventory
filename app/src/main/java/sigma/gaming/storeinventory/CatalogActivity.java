package sigma.gaming.storeinventory;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import sigma.gaming.storeinventory.data.ProductContract.ProductEntry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CatalogActivity extends AppCompatActivity {

    public static final String LOG_TAG = CatalogActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fb = findViewById(R.id.fab);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    public void InsertProduct(){

        ContentValues contentValues=new ContentValues();
        contentValues.put(ProductEntry.COLUMN_PRODUCT_NAME,getString(R.string.dummy_data_product_name));
        contentValues.put(ProductEntry.COLUMN_PRODUCT_MODEL,getString(R.string.dummy_data_product_model));
        contentValues.put(ProductEntry.COLUMN_PRODUCT_GRADE,getString(R.string.grade_new));
        contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY,7);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_PICTURE,getString(R.drawable.example));
        contentValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,getString(R.string.dummy_data_supplier_name));
        contentValues.put(ProductEntry.COLUMN_SUPPLIER_EMAIL,getString(R.string.dummy_data_supplier_email));
        contentValues.put(ProductEntry.COLUMN_PRODUCT_PRICE,49.99);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertProduct();
                return true;

            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    private void insertProduct() {
    }

    private void deleteAllProducts() {
    }


}