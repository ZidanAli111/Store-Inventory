package sigma.gaming.storeinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import sigma.gaming.storeinventory.data.ProductContract.ProductEntry;


public class ProductDbHelper extends SQLiteOpenHelper {


    public static final String LOG_TAG = ProductDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "storehouse.db";
    private static final int DATABASE_VERSION = 1;


    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                ProductEntry.TABLE_NAME + "(" +
                ProductEntry._ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ProductEntry.COLUMN_PRODUCT_NAME + "TEXT NOT NULL, " +
                ProductEntry.COLUMN_PRODUCT_MODEL + "TEXT, " +
                ProductEntry.COLUMN_PRODUCT_PRICE + "INTEGER NOT NULL, " +
                ProductEntry.COLUMN_PRODUCT_PICTURE + "TEXT NOT NULL, " +
                ProductEntry.COLUMN_PRODUCT_GRADE + "INTEGER NOT NULL, " +
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME + "TEXT NOT NULL, " +
                ProductEntry.COLUMN_SUPPLIER_EMAIL + "TEXT NOT NULL, " +
                ProductEntry.COLUMN_PRODUCT_QUANTITY + "INTEGER DEFAULT 0);";
        Log.v(LOG_TAG, SQL_CREATE_PRODUCTS_TABLE);

        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
