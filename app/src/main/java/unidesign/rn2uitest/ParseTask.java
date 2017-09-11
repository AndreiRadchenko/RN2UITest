package unidesign.rn2uitest;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import unidesign.rn2uitest.MySQLight.USSDSQLiteHelper;
import unidesign.rn2uitest.TempContentProvider.TempContentProvider;

import static java.security.AccessController.getContext;

/**
 * Created by United on 9/1/2017.
 */

public class ParseTask extends AsyncTask<Void, Void, String> {

    private Context mContext;
    public ProgressDialog pDialog;
    public unidesign.rn2uitest.RN_USSD pMA;
//    private ProgressDialog dialog = new ProgressDialog(mContext);

    public ParseTask (Context context, unidesign.rn2uitest.RN_USSD ma){
        mContext = context;
        pMA = ma;
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

        pDialog = new ProgressDialog(pMA);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);
        pDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        // получаем данные с внешнего ресурса
        try {
            //URL url = new URL("http://androiddocs.ru/api/friends.json");
            URL url = new URL("https://drive.google.com/uc?export=download&confirm=no_antivirus&id=0B6DUrz2vzeEjekJYVDNJVlhvQUU");
            //https://drive.google.com/open?id=0B6DUrz2vzeEjekJYVDNJVlhvQUU
            //https://github.com/AndreiRadchenko/RN2UITest/blob/6823ef1a7a4d608055affb0498bbe94b00791134/KyivstarUSSD.json

            urlConnection = (HttpURLConnection) url.openConnection();
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
        String name = "";

        Uri todoUri;
        // создаем объект для данных
        ContentValues values = new ContentValues();

        try {
            dataJsonObj = new JSONObject(strJson);
            JSONArray ussd = dataJsonObj.getJSONArray("USSD");

            // 1. достаем инфо о втором друге - индекс 1
            JSONObject firstUSSD = ussd.getJSONObject(0);
            name = firstUSSD.getString("name");
            Log.d(LOG_TAG, "First ussd: " + name);

            // 2. перебираем и выводим контакты каждого друга
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

                Log.d(LOG_TAG, "name: " + name1);
                Log.d(LOG_TAG, "template: " + template1);
                Log.d(LOG_TAG, "comment: " + comment1);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        pDialog.dismiss();
    }
}