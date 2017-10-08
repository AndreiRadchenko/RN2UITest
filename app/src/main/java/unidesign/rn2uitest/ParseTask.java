package unidesign.rn2uitest;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import unidesign.rn2uitest.MySQLight.USSDSQLiteHelper;
import unidesign.rn2uitest.TempContentProvider.TempContentProvider;

import static java.security.AccessController.getContext;

/**
 * Created by United on 9/1/2017.
 */

public class ParseTask extends AsyncTask<Void, Void, String> {

    public interface AsyncResponse {
        void processFinish(List<ImportRecyclerItem> output);
    }

    private Context mContext;
    public ProgressDialog pDialog;
    public unidesign.rn2uitest.ImportTemplateActivity ITA;
    URL mURL;
    public List<ImportRecyclerItem> mlistItems = new ArrayList<>();

    public AsyncResponse listener = null;//Call back interface


    public ParseTask (Context context, unidesign.rn2uitest.ImportTemplateActivity ma, URL url){
        mContext = context;
        ITA = ma;
        mURL = url;
        listener = ma;
//        delegate = asyncResponse;//Assigning call back interfacethrough constructor
    }

    public static String LOG_TAG = "my_log";

    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String resultJson = "";

    /** progress dialog to show user that the backup is processing. */
    /** application context. */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pDialog = new ProgressDialog(ITA);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);
        pDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        // получаем данные с внешнего ресурса
        try {

            //URL url = new URL("https://drive.google.com/uc?export=download&confirm=no_antivirus&id=0B6DUrz2vzeEjekJYVDNJVlhvQUU");

            urlConnection = (HttpURLConnection) mURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            resultJson = buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultJson;
    }

    @Override
    protected void onPostExecute(String strJson) {
        super.onPostExecute(strJson);
        // выводим целиком полученную json-строку
        Log.d(LOG_TAG, strJson);

        JSONObject dataJsonObj = null;
        String data_fild = null;
        String name = "";

        Uri todoUri;
        // создаем объект для данных
        ContentValues values = new ContentValues();
//        List<ImportRecyclerItem> mlistItems = new ArrayList<>();

        try {
            dataJsonObj = new JSONObject(strJson);
            data_fild = dataJsonObj.getString("data");
            //Log.d(LOG_TAG, "onPostExecute, data_fild: "+ data_fild);

            if (data_fild.compareTo("import templates") == 0) {
                //Log.d(LOG_TAG, "onPostExecute, data_fild: "+ data_fild);

                JSONArray templates = dataJsonObj.getJSONArray("templates");

                for (int i = 0; i < templates.length(); i++) {
                    JSONObject templates_one = templates.getJSONObject(i);

                    mlistItems.add(new ImportRecyclerItem(templates_one.getString("name"),
                            templates_one.getString("templatename"), templates_one.getString("jsondirref"),
                            templates_one.getString("pngdirref"), templates_one.getString("templatetype")));
                };

                //delegate.processFinish(mlistItems);
//                ITA.listItems.clear();
//                ITA.listItems.addAll(mlistItems);
//                Log.d(LOG_TAG, "onPostExecute, mlistItems.get(0).getTemplatename(): "+ mlistItems.get(0).getTemplatename());
                //delegate.processFinish(mlistItems);
                listener.processFinish(mlistItems);
                pDialog.dismiss();
            }

            else if (data_fild.compareTo("USSD") == 0) {

                JSONArray ussd = dataJsonObj.getJSONArray("USSD");

                for (int i = 0; i < ussd.length(); i++) {
                    JSONObject ussd_one = ussd.getJSONObject(i);

                    String name1 = ussd_one.getString("name");
                    String template1 = ussd_one.getString("template");
                    String comment1 = ussd_one.getString("comment");

                    //ContentValues values = new ContentValues();
                    values.put(USSDSQLiteHelper.COLUMN_NAME, name1);
                    values.put(USSDSQLiteHelper.COLUMN_COMMENT, comment1);
                    values.put(USSDSQLiteHelper.COLUMN_TEMPLATE, template1);

                    // New todo
                    todoUri = mContext.getContentResolver().insert(
                            TempContentProvider.CONTENT_URI_USSD, values);
                }

                pDialog.dismiss();
                ITA.finish();
            }

            else if (data_fild.compareTo("SMS") == 0) {

                JSONArray sms = dataJsonObj.getJSONArray("SMS");

                for (int i = 0; i < sms.length(); i++) {
                    JSONObject sms_one = sms.getJSONObject(i);

                    String name1 = sms_one.getString("name");
                    String phone_number1 = sms_one.getString("phone_number");
                    String template1 = sms_one.getString("template");
                    String comment1 = sms_one.getString("comment");

                    //ContentValues values = new ContentValues();
                    values.put(USSDSQLiteHelper.COLUMN_NAME, name1);
                    values.put(USSDSQLiteHelper.COLUMN_PHONE_NUMBER, phone_number1);
                    values.put(USSDSQLiteHelper.COLUMN_COMMENT, comment1);
                    values.put(USSDSQLiteHelper.COLUMN_TEMPLATE, template1);

                    // New todo
                    todoUri = mContext.getContentResolver().insert(
                            TempContentProvider.CONTENT_URI_SMS, values);
                }

                pDialog.dismiss();
                ITA.finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

       // delegate.processFinish(mlistItems);
       // pDialog.dismiss();
    }

}