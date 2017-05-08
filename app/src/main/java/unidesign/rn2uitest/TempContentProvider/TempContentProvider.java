package unidesign.rn2uitest.TempContentProvider;

import android.content.ContentProvider;
import java.util.Arrays;
import java.util.HashSet;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import unidesign.rn2uitest.MySQLight.USSDSQLiteHelper;

/**
 * Created by United on 4/17/2017.
 */

public class TempContentProvider extends ContentProvider {

    // database
    private USSDSQLiteHelper database;
    static final String LOG_TAG = "myProviderLogs";

    // used for the UriMacher
    private static final int USSD = 10;
    private static final int USSD_ID = 20;
    private static final int SMS = 30;
    private static final int SMS_ID = 40;

    private static final String AUTHORITY = "unidesign.rn2uitest.TempContentProvider";

    private static final String BASE_PATH_USSD = "USSD_templates";
    private static final String BASE_PATH_SMS = "SMS_templates";
    public static final Uri CONTENT_URI_USSD = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH_USSD);
    public static final Uri CONTENT_URI_SMS = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH_SMS);

    public static final String CONTENT_TYPE_USSD = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/USSD_templates";
    public static final String CONTENT_ITEM_TYPE_USSD = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/USSD_templates";
    public static final String CONTENT_TYPE_SMS = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/SMS_templates";
    public static final String CONTENT_ITEM_TYPE_SMS = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/SMS_templates ";

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_USSD, USSD);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_USSD + "/#", USSD_ID);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SMS, SMS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SMS + "/#", SMS_ID);
    }

    @Override
    public boolean onCreate() {
        Log.d(LOG_TAG, "--- In OnCreate() TempContentProvider ---");
        database = new USSDSQLiteHelper(getContext());
        //database.open();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        //checkColumns(projection);

        int uriType = sURIMatcher.match(uri);

        switch (uriType) {
            case USSD:
                // Set the table
                queryBuilder.setTables(USSDSQLiteHelper.TABLE_USSD);
                Log.d(LOG_TAG, "--- In TempContentProvider query(all)---");
                break;
            case USSD_ID:
                // Set the table
                queryBuilder.setTables(USSDSQLiteHelper.TABLE_USSD);
                // adding the ID to the original query
                queryBuilder.appendWhere(USSDSQLiteHelper.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            case SMS:
                // Set the table
                queryBuilder.setTables(USSDSQLiteHelper.TABLE_SMS);
                Log.d(LOG_TAG, "--- In TempContentProvider query(all)---");
                break;
            case SMS_ID:
                // Set the table
                queryBuilder.setTables(USSDSQLiteHelper.TABLE_SMS);
                // adding the ID to the original query
                queryBuilder.appendWhere(USSDSQLiteHelper.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        Uri mUri;

        switch (uriType) {
            case USSD:
                id = sqlDB.insert(USSDSQLiteHelper.TABLE_USSD, null, values);
                mUri = Uri.parse(BASE_PATH_USSD + "/" + id);
                break;
            case SMS:
                id = sqlDB.insert(USSDSQLiteHelper.TABLE_SMS, null, values);
                mUri = Uri.parse(BASE_PATH_SMS + "/" + id);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return mUri;

/*        long id = 0;
        switch (uriType) {
            case USSD:
                id = sqlDB.insert(USSDSQLiteHelper.TABLE_USSD, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH_USSD + "/" + id);*/
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        Log.d(LOG_TAG, "--- In TempContentProvider delete---");
        switch (uriType) {
            case USSD:
                rowsDeleted = sqlDB.delete(USSDSQLiteHelper.TABLE_USSD, selection,
                        selectionArgs);
                break;
            case USSD_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    Log.d(LOG_TAG, "--- In TempContentProvider delete id = ---" + id);
                    rowsDeleted = sqlDB.delete(
                            USSDSQLiteHelper.TABLE_USSD,
                            USSDSQLiteHelper.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(
                            USSDSQLiteHelper.TABLE_USSD,
                            USSDSQLiteHelper.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case USSD:
                rowsUpdated = sqlDB.update(USSDSQLiteHelper.TABLE_USSD,
                        values,
                        selection,
                        selectionArgs);
                break;
            case USSD_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(USSDSQLiteHelper.TABLE_USSD,
                            values,
                            USSDSQLiteHelper.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(USSDSQLiteHelper.TABLE_USSD,
                            values,
                            USSDSQLiteHelper.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = { USSDSQLiteHelper.COLUMN_ID,
                USSDSQLiteHelper.COLUMN_NAME, USSDSQLiteHelper.COLUMN_COMMENT,
                USSDSQLiteHelper.COLUMN_TEMPLATE };
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(
                    Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(
                    Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException(
                        "Unknown columns in projection");
            }
        }
    }


}
