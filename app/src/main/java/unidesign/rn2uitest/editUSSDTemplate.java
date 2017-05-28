package unidesign.rn2uitest;

import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import unidesign.rn2uitest.MySQLight.TemplatesDataSource;
import unidesign.rn2uitest.MySQLight.USSDSQLiteHelper;
import unidesign.rn2uitest.MySQLight.USSD_Template;
import unidesign.rn2uitest.TempContentProvider.TempContentProvider;

/**
 * Created by United on 4/5/2017.
 */

public class editUSSDTemplate extends AppCompatActivity{

    final String LOG_TAG = "myLogs";

    Button btnAdd, btnRead, btnCancel;
    EditText etName, etComment, etTemplate;

    TemplatesDataSource dbHelper;
   // String[] projection = TemplatesDataSource.allUSSDColumns;

    private Uri todoUri;
    USSD_Template template = new USSD_Template();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ussd_template);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.ussd_toolbar);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        etName = (EditText) findViewById(R.id.etName);
        etComment = (EditText) findViewById(R.id.etComment);
        etTemplate = (EditText) findViewById(R.id.etTemplate);

        Bundle extras = getIntent().getExtras();

        // check from the saved Instance
        todoUri = (savedInstanceState == null) ? null : (Uri) savedInstanceState
                .getParcelable(TempContentProvider.CONTENT_ITEM_TYPE_USSD);

        // Or passed from the other activity
        if (extras != null) {
            todoUri = extras
                    .getParcelable(TempContentProvider.CONTENT_ITEM_TYPE_USSD);
            //Log.d(LOG_TAG, "--- In OnCreate() editUSSDTemplate ---" + todoUri);
            Cursor cursor = getContentResolver().query(todoUri, TemplatesDataSource.allUSSDColumns, null, null, null);
            cursor.moveToFirst();
            //template = MyAdapter.cursorToTemplate(cursor);
            //etName.setText(template.getName());
            //etComment.setText(template.getComment());
            //etTemplate.setText(template.getTemplate());
            etName.setText(cursor.getString(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_NAME)));
            etComment.setText(cursor.getString(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_COMMENT)));
            etTemplate.setText(cursor.getString(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_TEMPLATE)));
            //etName.setText("USSD");
            //etComment.setText("Comment");
            //etTemplate.setText("*111#");
            //fillData(todoUri);
        }

        // создаем объект для создания и управления версиями БД
        //dbHelper = new TemplatesDataSource(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ussd_appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // создаем объект для данных
        ContentValues values = new ContentValues();

        // получаем данные из полей ввода
        String name = etName.getText().toString();
        String comment = etComment.getText().toString();
        String template = etTemplate.getText().toString();

        switch (item.getItemId()) {
            case R.id.action_done:
                // User chose the "Settings" item, show the app settings UI...
/*                Snackbar.make(findViewById(R.id.ussd_toolbar), "Replace done with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                if (name.length() == 0 && template.length() == 0) {
                    return false;
                }

                //ContentValues values = new ContentValues();
                values.put(USSDSQLiteHelper.COLUMN_NAME, name);
                values.put(USSDSQLiteHelper.COLUMN_COMMENT, comment);
                values.put(USSDSQLiteHelper.COLUMN_TEMPLATE, template);

                if (todoUri == null) {
                    // New todo
                    todoUri = getContentResolver().insert(
                            TempContentProvider.CONTENT_URI_USSD, values);
                } else {
                    // Update todo
                    getContentResolver().update(todoUri, values, null, null);
                }

                //dbHelper.close();
                finish();

                return true;

            case R.id.action_copy:
                // User chose the "Settings" item, show the app settings UI...
                Snackbar.make(findViewById(R.id.ussd_toolbar), "Replace copy with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                return true;

            case R.id.action_settings:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
