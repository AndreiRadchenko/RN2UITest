package unidesign.rn2uitest.MySQLight;

/**
 * Created by United on 4/4/2017.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
//import unidesign.rn2uitest.MySQLight.USSDSQLightHelper;

public class TemplatesDataSource {
    // Database fields
    private SQLiteDatabase database;
    private USSDSQLiteHelper dbHelper;
    public static String[] allUSSDColumns = { USSDSQLiteHelper.COLUMN_ID, USSDSQLiteHelper.COLUMN_NAME,
            USSDSQLiteHelper.COLUMN_COMMENT, USSDSQLiteHelper.COLUMN_TEMPLATE, USSDSQLiteHelper.COLUMN_IMAGE };
    public static String[] allSMSColumns = { USSDSQLiteHelper.COLUMN_ID, USSDSQLiteHelper.COLUMN_NAME,
            USSDSQLiteHelper.COLUMN_COMMENT, USSDSQLiteHelper.COLUMN_PHONE_NUMBER, USSDSQLiteHelper.COLUMN_TEMPLATE,
            USSDSQLiteHelper.COLUMN_IMAGE};

    public TemplatesDataSource(Context context) {
        dbHelper = new USSDSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public USSD_Template createTemplate(String name, String comment, String template) {
        ContentValues values = new ContentValues();
        values.put(USSDSQLiteHelper.COLUMN_NAME, name);
        values.put(USSDSQLiteHelper.COLUMN_COMMENT, comment);
        values.put(USSDSQLiteHelper.COLUMN_TEMPLATE, template);
        long insertId = database.insert(USSDSQLiteHelper.TABLE_USSD, null,
                values);
        Cursor cursor = database.query(USSDSQLiteHelper.TABLE_USSD,
                allUSSDColumns, USSDSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        USSD_Template newTemplate = cursorToTemplate(cursor);
        cursor.close();
        return newTemplate;
    }

    public void deleteTemplate(long template_id) {
        //long id = template.getId();
        long id = template_id;
        //System.out.println("Comment deleted with id: " + id);
        database.delete(USSDSQLiteHelper.TABLE_USSD, USSDSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<USSD_Template> getAllTemplates() {
        List<USSD_Template> templates = new ArrayList<USSD_Template>();

        Cursor cursor = database.query(USSDSQLiteHelper.TABLE_USSD,
                allUSSDColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            USSD_Template template = cursorToTemplate(cursor);
            templates.add(template);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return templates;
    }

    private USSD_Template cursorToTemplate(Cursor cursor) {
        USSD_Template template = new USSD_Template();
        template.setId(cursor.getLong(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_ID)));
        template.setName(cursor.getString(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_NAME)));
        template.setComment(cursor.getString(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_COMMENT)));
        template.setTemplate(cursor.getString(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_TEMPLATE)));
        template.setImage(cursor.getString(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_IMAGE)));
        return template;
    }
}
