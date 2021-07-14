package sigma.gaming.storeinventory.data;

import android.provider.BaseColumns;

public final class ProductContract {



    private ProductContract() {
    }

    public static final class ProductEntry implements BaseColumns {

        public final static String TABLE_NAME = "products";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_PRODUCT_NAME = "name";

        public final static String COLUMN_PRODUCT_MODEL = "model";

        public final static String COLUMN_PRODUCT_PRICE = "price";

        public static final String COLUMN_PRODUCT_PICTURE = "picture";

        public final static String COLUMN_PRODUCT_GRADE = "grade";

        public final static String COLUMN_PRODUCT_SUPPLIER_NAME = "supplierName";

        public final static String COLUMN_SUPPLIER_EMAIL = "supplierEmail";

        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";


        public static final int GRADE_UNKNOWN = 0;
        public static final int GRADE_NEW = 1;
        public static final int GRADE_USED = 2;

        public static  boolean isValidGrade(int grade){

            if(grade==GRADE_UNKNOWN||grade==GRADE_NEW||grade==GRADE_USED){
                return true;
            }
        return false;
        }


    }


}
