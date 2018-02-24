package unidesign.ussdsmscodes.ImportActivity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.ActionMode;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import unidesign.ussdsmscodes.MySQLight.USSDSQLiteHelper;
import unidesign.ussdsmscodes.R;
import unidesign.ussdsmscodes.RN_USSD;
import unidesign.ussdsmscodes.TempContentProvider.TempContentProvider;

/**
 * Created by United on 9/1/2017.
 */

public class ParseTask extends AsyncTask<String, Void, String> {

    public interface AsyncResponse {
        void processFinish(List<Object> output);
    }

    private Context mContext;
    public ProgressDialog pDialog;
    public ImportTemplateActivity ITA;
    URL mURL;
    public List<Object> mlistItems = new ArrayList<>();

    public AsyncResponse listener = null;//Call back interface
    public String ImageName;


    public ParseTask(Context context, ImportTemplateActivity ma, URL url) {
        mContext = context;
        ITA = ma;
        mURL = url;
        listener = ma;
//        delegate = asyncResponse;//Assigning call back interfacethrough constructor
    }

    public static String LOG_TAG = "ParseTask";

    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String resultJson = "";

    /** progress dialog to show user that the backup is processing. */
    /**
     * application context.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        ConnectivityManager ConnectionManager = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
//        if (networkInfo != null && networkInfo.isConnected() == true) {
            pDialog = new ProgressDialog(ITA);
            pDialog.setMessage(mContext.getString(R.string.LoadDialogText));
            pDialog.setCancelable(true);
            pDialog.show();

    }

    @Override
    protected String doInBackground(String... params) {

        ImageName = params[0];
//        Log.d(LOG_TAG, "-- ImageName --" + ImageName);
        resultJson = "noInternet";
        if (isOnline())
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
        //Log.d(LOG_TAG, strJson);

        JSONObject dataJsonObj = null;
        String data_fild = null;
        String name = "";

        Uri todoUri;
        // создаем объект для данных
        ContentValues values = new ContentValues();
//        List<ImportRecyclerItem> mlistItems = new ArrayList<>();
        if(!strJson.equals("noInternet"))
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
                    }

                    //delegate.processFinish(mlistItems);
    //                ITA.listItems.clear();
    //                ITA.listItems.addAll(mlistItems);
    //                Log.d(LOG_TAG, "onPostExecute, mlistItems.get(0).getTemplatename(): "+ mlistItems.get(0).getTemplatename());
                    //delegate.processFinish(mlistItems);
                    listener.processFinish(mlistItems);
                    pDialog.dismiss();
                } else if (data_fild.compareTo("USSD") == 0) {

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
                        values.put(USSDSQLiteHelper.COLUMN_IMAGE, ImageName);

                        //Log.d(LOG_TAG, "-- ImageName --" + ImageName);
                        todoUri = mContext.getContentResolver().insert(
                                TempContentProvider.CONTENT_URI_USSD, values);
                    }

                    RN_USSD.current_tab = RN_USSD.USSD_TAB;
                    pDialog.dismiss();
                    ITA.finish();

                } else if (data_fild.compareTo("SMS") == 0) {

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
                        values.put(USSDSQLiteHelper.COLUMN_IMAGE, ImageName);

                        todoUri = mContext.getContentResolver().insert(
                                TempContentProvider.CONTENT_URI_SMS, values);
                    }

                    RN_USSD.current_tab = RN_USSD.SMS_TAB;
                    pDialog.dismiss();
                    ITA.finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        else {
            // no internet connnection;
            pDialog.dismiss();
            if (RN_USSD.prefManager.isShowMainDemo() )
                RN_USSD.prefManager.setShowMainDemo(false);
            Toast.makeText(ITA, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            //RN_USSD.prefManager.setShowMainDemo(false);
        }

    }

    // TCP/HTTP/DNS (depending on the port, 53=DNS, 80=HTTP, etc.)
    public boolean isOnline() {
        try {
            int timeoutMs = 2000;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) { return false; }
    }

}