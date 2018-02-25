package unidesign.ussdsmscodes.SettingsTools;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import unidesign.ussdsmscodes.ImportActivity.ImportRecyclerItem;
import unidesign.ussdsmscodes.MySQLight.USSDSQLiteHelper;
import unidesign.ussdsmscodes.RN_USSD;
import unidesign.ussdsmscodes.TempContentProvider.TempContentProvider;

/**
 * Created by United on 12/29/2017.
 */

public class RestoreTask extends AsyncTask<RestoreRecyclerItem, Void, String> {

    public interface AsyncResponse {
        void processFinish(String backup_name);
    }

    private Context mContext;
    public ProgressDialog pDialog;
    public RestoreActivity RA;
    public List<ImportRecyclerItem> mlistItems = new ArrayList<>();

    public RestoreTask.AsyncResponse listener = null;//Call back interface
    public String ImageName;


    public RestoreTask(Context context, RestoreActivity ra) {
        mContext = context;
        RA = ra;
        listener = ra;
//        delegate = asyncResponse;//Assigning call back interfacethrough constructor
    }

    public static String LOG_TAG = "RestoreTask";

    BufferedReader reader = null;
    String resultJson = "";

    /** progress dialog to show user that the backup is processing. */
    /**
     * application context.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

/*        pDialog = new ProgressDialog(ITA);
        pDialog.setMessage(mContext.getString(R.string.LoadDialogText));
        pDialog.setCancelable(true);
        pDialog.show();*/
    }

    @Override
    protected String doInBackground(RestoreRecyclerItem... params) {

        RestoreRecyclerItem item = params[0];
//        Log.d(LOG_TAG, "-- ImageName --" + ImageName);
        // получаем данные с карты памяти
        String USSDJson = getStringFromFile(item.getUSSD_file_path());
        String SMSJson = getStringFromFile(item.getSMS_file_path());
        restoreJSON2DB(USSDJson);
        restoreJSON2DB(SMSJson);


        return item.getName();
    }

    @Override
    protected void onPostExecute(String strJson) {
        super.onPostExecute(strJson);
        listener.processFinish(strJson);
        //RN_USSD.closeDriwer = true;
        RN_USSD.setRecycleViewToBottom = true;
        RA.finish();
    }

    String getStringFromFile(String path){

        String line,line1 = "";

        try
        {
            File ussdfile = new File(path);
            FileInputStream instream = new FileInputStream(ussdfile);
            //InputStream instream = openFileInput("E:\\test\\src\\com\\test\\mani.txt");
            if (instream != null)
            {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);

                try
                {
                    while ((line = buffreader.readLine()) != null)
                        line1+=line;
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                buffreader.close();
            }
            instream.close();
        }
        catch (Exception e)
        {
            String error="";
            error=e.getMessage();
        }
        return line1;
    }

    void restoreJSON2DB (String jsonstr){

        JSONObject dataJsonObj = null;
        String data_fild = null;
        String name = "";

        Uri todoUri;
        // создаем объект для данных
        ContentValues values = new ContentValues();

        try {
            dataJsonObj = new JSONObject(jsonstr);
            data_fild = dataJsonObj.getString("data");
            //Log.d(LOG_TAG, "onPostExecute, data_fild: "+ data_fild);

            if (data_fild.compareTo("USSD") == 0) {

                JSONArray ussd = dataJsonObj.getJSONArray("USSD");

                for (int i = 0; i < ussd.length(); i++) {
                    JSONObject ussd_one = ussd.getJSONObject(i);

                    String name1 = ussd_one.getString("name");
                    String template1 = ussd_one.getString("template");
                    String comment1 = ussd_one.getString("comment");
                    String image1 = ussd_one.getString("image");

                    //ContentValues values = new ContentValues();
                    values.put(USSDSQLiteHelper.COLUMN_NAME, name1);
                    values.put(USSDSQLiteHelper.COLUMN_COMMENT, comment1);
                    values.put(USSDSQLiteHelper.COLUMN_TEMPLATE, template1);
                    values.put(USSDSQLiteHelper.COLUMN_IMAGE, image1);

                    //Log.d(LOG_TAG, "-- ImageName --" + ImageName);
                    todoUri = mContext.getContentResolver().insert(
                            TempContentProvider.CONTENT_URI_USSD, values);
                }

            } else if (data_fild.compareTo("SMS") == 0) {

                JSONArray sms = dataJsonObj.getJSONArray("SMS");

                for (int i = 0; i < sms.length(); i++) {
                    JSONObject sms_one = sms.getJSONObject(i);

                    String name1 = sms_one.getString("name");
                    String phone_number1 = sms_one.getString("phone_number");
                    String template1 = sms_one.getString("template");
                    String comment1 = sms_one.getString("comment");
                    String image1 = sms_one.getString("image");

                    //ContentValues values = new ContentValues();
                    values.put(USSDSQLiteHelper.COLUMN_NAME, name1);
                    values.put(USSDSQLiteHelper.COLUMN_PHONE_NUMBER, phone_number1);
                    values.put(USSDSQLiteHelper.COLUMN_COMMENT, comment1);
                    values.put(USSDSQLiteHelper.COLUMN_TEMPLATE, template1);
                    values.put(USSDSQLiteHelper.COLUMN_IMAGE, image1);

                    todoUri = mContext.getContentResolver().insert(
                            TempContentProvider.CONTENT_URI_SMS, values);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
