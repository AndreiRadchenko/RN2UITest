package unidesign.ussdsmscodes;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import unidesign.ussdsmscodes.MySQLight.TemplatesDataSource;
import unidesign.ussdsmscodes.MySQLight.USSDSQLiteHelper;
import unidesign.ussdsmscodes.MySQLight.USSD_Template;
import unidesign.ussdsmscodes.TempContentProvider.TempContentProvider;

/**
 * Created by United on 4/5/2017.
 */

public class editSMSTemplate extends AppCompatActivity {

    final String LOG_TAG = "editSMSTemplate";

    EditText etName, etComment, etPhone, etTemplate;
    String image_file = "";
    boolean newsms;

    private Uri todoUri;
    USSD_Template template = new USSD_Template();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_template);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.sms_toolbar);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        etName = (EditText) findViewById(R.id.etName);
        etComment = (EditText) findViewById(R.id.etComment);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etTemplate = (EditText) findViewById(R.id.etTemplate);

        Bundle extras = getIntent().getExtras();
        String action = getIntent().getAction();
        newsms = action.equals("intent.action.newsms");
        // check from the saved Instance
        todoUri = (savedInstanceState == null) ? null : (Uri) savedInstanceState
                .getParcelable(TempContentProvider.CONTENT_ITEM_TYPE_SMS);

        // Or passed from the other activity
        if (extras != null) {
            todoUri = extras
                    .getParcelable(TempContentProvider.CONTENT_ITEM_TYPE_SMS);
            //Log.d(LOG_TAG, "--- In OnCreate() editUSSDTemplate ---" + todoUri);
            Cursor cursor = getContentResolver().query(todoUri, TemplatesDataSource.allSMSColumns, null, null, null);
            cursor.moveToFirst();
            //template = MyAdapter.cursorToTemplate(cursor);
            //etName.setText(template.getName());
            //etComment.setText(template.getComment());
            //etTemplate.setText(template.getTemplate());
            etName.setText(cursor.getString(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_NAME)));
            etComment.setText(cursor.getString(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_COMMENT)));
            etPhone.setText(cursor.getString(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_PHONE_NUMBER)));
            etTemplate.setText(cursor.getString(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_TEMPLATE)));
            image_file = cursor.getString(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_IMAGE));
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
        // получаем данные из полей ввода
        String name = etName.getText().toString();
        String comment = etComment.getText().toString();
        String phone = etPhone.getText().toString();
        String template = etTemplate.getText().toString();

        switch (item.getItemId()) {
            case android.R.id.home:
                {
                    finish();
                    return true;
                }
            case R.id.action_done:
                // User chose the "Settings" item, show the app settings UI...
/*                Snackbar.make(findViewById(R.id.ussd_toolbar), "Replace done with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                if (name.length() == 0 && template.length() == 0) {
                    Snackbar.make(findViewById(R.id.sms_toolbar), R.string.snackbar_fill_form, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return false;
                }

                values.put(USSDSQLiteHelper.COLUMN_NAME, name);
                values.put(USSDSQLiteHelper.COLUMN_COMMENT, comment);
                values.put(USSDSQLiteHelper.COLUMN_PHONE_NUMBER, phone);
                values.put(USSDSQLiteHelper.COLUMN_TEMPLATE, template);
                values.put(USSDSQLiteHelper.COLUMN_IMAGE, image_file);

                if (todoUri == null) {
                    todoUri = getContentResolver().insert(
                            TempContentProvider.CONTENT_URI_SMS, values);
                } else {
                    getContentResolver().update(todoUri, values, null, null);
                }

                //dbHelper.close();
                if (newsms)
                    RN_USSD.setRecycleViewToBottom = true;
                Toast.makeText(getApplication(), R.string.template_saved, Toast.LENGTH_LONG).show();
                finish();

                return true;

            case R.id.action_copy:
                // User chose the "Settings" item, show the app settings UI...
/*                Snackbar.make(findViewById(R.id.ussd_toolbar), "Replace copy with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                if (name.length() == 0 && template.length() == 0) {
                    Snackbar.make(findViewById(R.id.sms_toolbar), R.string.snackbar_fill_form, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return false;
                }

                values.put(USSDSQLiteHelper.COLUMN_NAME, name);
                values.put(USSDSQLiteHelper.COLUMN_COMMENT, comment);
                values.put(USSDSQLiteHelper.COLUMN_PHONE_NUMBER, phone);
                values.put(USSDSQLiteHelper.COLUMN_TEMPLATE, template);
                values.put(USSDSQLiteHelper.COLUMN_IMAGE, image_file);

                todoUri = getContentResolver().insert(
                        TempContentProvider.CONTENT_URI_SMS, values);

                //dbHelper.close();
                RN_USSD.setRecycleViewToBottom = true;
                Toast.makeText(getApplication(), R.string.new_template_aded, Toast.LENGTH_LONG).show();
                finish();

                return true;

/*            case R.id.action_settings:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;*/

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}

