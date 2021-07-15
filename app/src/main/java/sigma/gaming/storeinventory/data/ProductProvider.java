package sigma.gaming.storeinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import sigma.gaming.storeinventory.data.ProductContract.ProductEntry;

public class ProductProvider extends ContentProvider {


    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    private ProductDbHelper mDbHelper;

    @Override
    public boolean onCreate() {

        mDbHelper = new ProductDbHelper(getContext());

        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))

                };
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);

        }


        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final  int match=sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                return insertProduct(uri,values);
            default: throw new IllegalArgumentException("Insertion is not supported for "+uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {

        String name=values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if(name==null){
            throw new IllegalArgumentException("Product name is Required.");
        }

        /*******************************************************************/

        Integer grade=values.getAsInteger(ProductEntry.COLUMN_PRODUCT_GRADE);
        if(grade==null|| !ProductEntry.isValidGrade(grade))

        {
         throw new IllegalArgumentException("Product required valid Grade.");
        }

        /*******************************************************************/

        Integer quantity=values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if(quantity==null&&quantity<0)
        {
            throw new IllegalArgumentException("Product required valid quantity");

        }
        /*******************************************************************/


        SQLiteDatabase database=mDbHelper.getWritableDatabase();

        long id=database.insert(ProductEntry.TABLE_NAME,null,values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);


        }

        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database=mDbHelper.getWritableDatabase();
        int rowDeleted;

        final int match=sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS : rowDeleted=database.delete(ProductEntry.TABLE_NAME,selection,selectionArgs);
            break;
            case PRODUCT_ID: selection=ProductEntry._ID+"=?";
            selectionArgs=new String[]{
                    String.valueOf(ContentUris.parseId(uri))
            };
            rowDeleted=database.delete(ProductEntry.TABLE_NAME,selection,selectionArgs);
            break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for "+uri);
        }


        return rowDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                // For the PRODUCT_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if(values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)){
            String name=values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if(name==null){
                throw new IllegalArgumentException("Product require a name");
            }
        }

        if(values.containsKey(ProductEntry.COLUMN_PRODUCT_GRADE)){
            Integer grade=values.getAsInteger(ProductEntry.COLUMN_PRODUCT_GRADE);
            if(grade==null||!ProductEntry.isValidGrade(grade)){
                throw new IllegalArgumentException("Product require valid Grade");

            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            // Check that the quantity is greater than or equal to 0 kg
            Integer quantity = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database=mDbHelper.getWritableDatabase();
        int rowUpdated=database.update(ProductEntry.TABLE_NAME,values,selection,selectionArgs);
        return  rowUpdated;
    }
}
