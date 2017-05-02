package unidesign.rn2uitest;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
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

public class editUSSDTemplate extends AppCompatActivity implements OnClickListener{

    final String LOG_TAG = "myLogs";

    Button btnAdd, btnRead, btnCancel;
    EditText etName, etComment, etTemplate;

    TemplatesDataSource dbHelper;
   // String[] projection = TemplatesDataSource.allColumns;

    private Uri todoUri;
    USSD_Template template = new USSD_Template();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ussd_template);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnRead = (Button) findViewById(R.id.btnRead);
        btnRead.setOnClickListener(this);

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        etName = (EditText) findViewById(R.id.etName);
        etComment = (EditText) findViewById(R.id.etComment);
        etTemplate = (EditText) findViewById(R.id.etTemplate);

        Bundle extras = getIntent().getExtras();

        // check from the saved Instance
        todoUri = (savedInstanceState == null) ? null : (Uri) savedInstanceState
                .getParcelable(TempContentProvider.CONTENT_ITEM_TYPE);

        // Or passed from the other activity
        if (extras != null) {
            todoUri = extras
                    .getParcelable(TempContentProvider.CONTENT_ITEM_TYPE);
            //Log.d(LOG_TAG, "--- In OnCreate() editUSSDTemplate ---" + todoUri);
            Cursor cursor = getContentResolver().query(todoUri, TemplatesDataSource.allColumns, null, null, null);
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
    public void onClick(View v) {

        // создаем объект для данных
        ContentValues values = new ContentValues();

        // получаем данные из полей ввода
        String name = etName.getText().toString();
        String comment = etComment.getText().toString();
        String template = etTemplate.getText().toString();

        // подключаемся к БД
        //dbHelper.open();


        switch (v.getId()) {
            case R.id.btnAdd:
                Log.d(LOG_TAG, "--- Insert in mytable: ---");
                // подготовим данные для вставки в виде пар: наименование столбца - значение
                //USSD_Template ussd_template = dbHelper.createTemplate(name, comment, template);
                //dbHelper.createTemplate(name, comment, template);
                //Log.d(LOG_TAG, "row inserted, ID = " + ussd_template.getId());

                // only save if either summary or description
                // is available

                if (name.length() == 0 && template.length() == 0) {
                    return;
                }

                //ContentValues values = new ContentValues();
                values.put(USSDSQLiteHelper.COLUMN_NAME, name);
                values.put(USSDSQLiteHelper.COLUMN_COMMENT, comment);
                values.put(USSDSQLiteHelper.COLUMN_TEMPLATE, template);

                if (todoUri == null) {
                    // New todo
                    todoUri = getContentResolver().insert(
                            TempContentProvider.CONTENT_URI, values);
                } else {
                    // Update todo
                    getContentResolver().update(todoUri, values, null, null);
                }

                //dbHelper.close();
                finish();
                break;
            case R.id.btnRead:
                Log.d(LOG_TAG, "--- Button Read Pressed ---");

                break;
            case R.id.btnCancel:
                Log.d(LOG_TAG, "--- Cancel Button Pressed: ---");

                //dbHelper.close();
                finish();
                break;
        }
        // закрываем подключение к БД
        //dbHelper.close();
    }

}
