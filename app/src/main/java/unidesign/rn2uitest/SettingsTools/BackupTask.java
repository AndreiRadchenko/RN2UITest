package unidesign.rn2uitest.SettingsTools;

import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.UserDictionary;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import unidesign.rn2uitest.MyAdapter;
import unidesign.rn2uitest.MySQLight.TemplatesDataSource;
import unidesign.rn2uitest.MySQLight.USSD_Template;
import unidesign.rn2uitest.R;
import unidesign.rn2uitest.TempContentProvider.TempContentProvider;

import static android.app.PendingIntent.getActivity;
import static com.h6ah4i.android.widget.advrecyclerview.animator.impl.ItemMoveAnimationManager.TAG;

/**
 * Created by United on 12/8/2017.
 */

public class BackupTask extends AsyncTask<String, Integer, String> {

    public static final String DIR_SD = "RN2backup";
    String SMSfile = "SMS_templates";
    String USSDfile = "USSD_templates";
    private Context mContext;

    public  BackupTask(Context context){
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(mContext, R.string.no_sdcard, Toast.LENGTH_LONG).show();
            return null;
        }

        File sdPath = new File(Environment.getExternalStorageDirectory()
                + File.separator + DIR_SD);
        if (!sdPath.exists()) {
            // ñîçäàåì êàòàëîã
            sdPath.mkdirs();
        }

/*        File imagePath = new File(sdPath + "/" + "icons");
        if (!imagePath.exists()) {
            imagePath.mkdirs();
        }*/

        File appIconPath = mContext.getFilesDir() ;
        appIconPath = new File(appIconPath.getPath() + "/" + "icons");
        if (appIconPath.exists()) {
            //appIconPath.listFiles().clone();
            Log.d("doInBackground: ", "image copyed into: " );
            copyFileOrDirectory(appIconPath.getPath(), sdPath.getPath());
        }

        Log.d("doInBackground: ", "sdPath.exists() " + sdPath +" "+ sdPath.exists());

        SimpleDateFormat sdf = new SimpleDateFormat("_yy-MM-dd_HH-mm");
        // String mydate =
        // java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        String mydate = sdf.format(Calendar.getInstance().getTime());
        // Log.d("ExportCSVActivity", mydate);

        SMSfile = SMSfile + mydate + ".json";
        USSDfile = USSDfile + mydate + ".json";
        File smssdFile = new File(sdPath, SMSfile);
        File ussdsdFile = new File(sdPath, USSDfile);

        //Log.d("doInBackground: ", String.valueOf(ussdsdFile));

        FileOutputStream fos = null;
        FileOutputStream fos_sms = null;

        try {
            ussdsdFile.createNewFile();
            smssdFile.createNewFile();
            fos = new FileOutputStream(ussdsdFile,true);
            fos_sms = new FileOutputStream(smssdFile,true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintStream ps = new PrintStream(fos);
        PrintStream ps_sms = new PrintStream(fos_sms);

        ps.append(db2JSON(TempContentProvider.CONTENT_URI_USSD).toString());
        ps_sms.append(db2JSON(TempContentProvider.CONTENT_URI_SMS).toString());

        String result = mydate;
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result != null) {
//            Toast.makeText(mContext, mContext.getResources().getString(R.string.Backup_sucsess, result),
//                    Toast.LENGTH_LONG).show();
            Toast.makeText(mContext, "Backup is added", Toast.LENGTH_LONG).show();
        }
    }



    public  void copyFileOrDirectory(String srcDir, String destDir){
        try{
            File src = new File(srcDir);
            File dst = new File(destDir, src.getName());

            if (src.isDirectory()){
                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i])).getPath();
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);
                }
            }
            else {
                copyFile(src, dst);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public  void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists())
            destFile.createNewFile();

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    private void deleteFile(String inputPath, String inputFile) {
        try {
            // delete the original file
            new File(inputPath + inputFile).delete();
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

public JSONObject db2JSON(Uri uri) {

    List<USSD_Template> templates = new ArrayList<USSD_Template>();
    USSD_Template template;

    //Uri uri = TempContentProvider.CONTENT_URI_USSD;
    Cursor mCursor = null;

    if (uri == TempContentProvider.CONTENT_URI_USSD){
        mCursor = mContext.getContentResolver().query(uri,
                TemplatesDataSource.allUSSDColumns, null, null, null);
    }
    else {
        mCursor = mContext.getContentResolver().query(uri,
                TemplatesDataSource.allSMSColumns, null, null, null);
    }

    mCursor.moveToFirst();

    while (!mCursor.isAfterLast()) {
        template = MyAdapter.cursorToTemplate(mCursor);
        templates.add(template);
        mCursor.moveToNext();
    }

    JSONObject tableObject = new JSONObject();
    JSONArray ussdArray = new JSONArray();

    int i = 0;
    for (USSD_Template mtemplate: templates) {
        JSONObject rObject = new JSONObject();
        try {
            rObject.put("id", mtemplate.getId());
            rObject.put("name", mtemplate.getName());
            rObject.put("template", mtemplate.getTemplate());
            if (mtemplate.getPhone() != null) {
                rObject.put("Phone", mtemplate.getPhone());
            }
            rObject.put("comment", mtemplate.getComment());
            rObject.put("image", mtemplate.getImage());

            ussdArray.put(i, rObject);
            i++;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    try {
        if (uri == TempContentProvider.CONTENT_URI_USSD) {
            tableObject.put("data", "USSD");
            //tableObject.put("data1", "dbUSSDKyivstar");
            tableObject.put("USSD", ussdArray);
        }
        else {
            tableObject.put("data", "SMS");
            tableObject.put("SMS", ussdArray);
        }
    } catch (JSONException e) {
        e.printStackTrace();
    }

    return tableObject;
}


















}
