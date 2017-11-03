package unidesign.rn2uitest.MySQLight;

/**
 * Created by United on 4/4/2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class USSDSQLiteHelper extends SQLiteOpenHelper{

    public static final String TABLE_USSD = "USSD_templates";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_TEMPLATE = "template";
    public static final String COLUMN_IMAGE = "image";

    public static final String TABLE_SMS = "SMS_templates";
    //public static final String COLUMN_ID = "_id";
    //public static final String COLUMN_NAME = "name";
    //public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";

    private static final String DATABASE_NAME = "templates.db";
    private static final int DATABASE_VERSION = 3;

    // Database creation sql statement
    private static final String DATABASE_USSD_CREATE = "create table " + TABLE_USSD +
            "( " + COLUMN_ID + " integer primary key autoincrement, " +
                   COLUMN_NAME + " text not null, " +
                   COLUMN_COMMENT + " text not null, " +
            COLUMN_TEMPLATE + " text not null, " +
            COLUMN_IMAGE + " text not null);";

    private static final String DATABASE_SMS_CREATE = "create table " + TABLE_SMS +
            "( " + COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_NAME + " text not null, " +
            COLUMN_COMMENT + " text not null, " +
            COLUMN_PHONE_NUMBER + " text not null, " +
            COLUMN_TEMPLATE + " text not null, " +
            COLUMN_IMAGE + " text not null);";

    public USSDSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_USSD_CREATE);
        database.execSQL(DATABASE_SMS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
/*        Log.w(USSDSQLightHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");*/
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USSD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS);
        onCreate(db);
    }

}
