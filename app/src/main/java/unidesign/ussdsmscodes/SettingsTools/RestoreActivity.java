package unidesign.ussdsmscodes.SettingsTools;

import android.content.ContentValues;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import unidesign.ussdsmscodes.R;

/**
 * Created by United on 12/26/2017.
 */

public class RestoreActivity extends AppCompatActivity implements RestoreTask.AsyncResponse {


    static final String LOG_TAG = "RestoreTemplateActivity";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    public RestoreTemplateAdapter mAdapter;
    ContentValues values = new ContentValues();
    public List<RestoreRecyclerItem> listItems = new ArrayList<>();

    List<String> myFileArray = new ArrayList<>();
    String BackupName = "";
    File sdPath;
    public ActionMode mActionMode;
    public RestoreTask AsyncRestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restore_activity);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.restore_toolbar);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        recyclerView=(RecyclerView)findViewById(R.id.restore_layout);

        mLayoutManager = new LinearLayoutManager(this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        myFileArray.clear();

        sdPath = new File(Environment.getExternalStorageDirectory()
                + File.separator + BackupTask.DIR_SD);
        if (!sdPath.exists()) {
            // ñîçäàåì êàòàëîã
            myFileArray.add("Nothing to restore");
        }
        else {
                String files[] = sdPath.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    RestoreRecyclerItem Backupitem = getBackup(files, files[i]);
                    if (!Backupitem.getName().equals(""))
                        listItems.add(Backupitem);
                }
        }

        //Log.d(LOG_TAG, "initializeData(): "+ listItems.get(0).getTemplatename());
        final RestoreActivity RA = this;

        Collections.sort(listItems, new Comparator<RestoreRecyclerItem>() {
            @Override
            public int compare(RestoreRecyclerItem obj1, RestoreRecyclerItem obj2) {
                return obj1.getName().compareToIgnoreCase(obj2.getName());
            }
        });

        mAdapter = new RestoreTemplateAdapter(this, listItems, new RestoreTemplateAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RestoreRecyclerItem item) {
                try {
                    AsyncRestore = new RestoreTask (getApplicationContext(), RA);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                AsyncRestore.execute(item);
                // copy icon dir from external storage to application dir
                File appIconPath = getApplicationContext().getFilesDir() ;
                appIconPath = new File(appIconPath.getPath());
                if (!appIconPath.exists()) {
                    appIconPath.mkdirs();
                }
                String backup_icons_dir = sdPath.getPath() + "/" + "icons";
                copyFileOrDirectory(backup_icons_dir, appIconPath.getPath());
            }
        });

        RestoreSwipeAndDragHelper swipeAndDragHelper = new RestoreSwipeAndDragHelper(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeAndDragHelper);
        //mAdapter.setTouchHelper(touchHelper);
        recyclerView.setAdapter(mAdapter);
        //touchHelper.attachToRecyclerView(recyclerView);
        implementRecyclerViewClickListeners();

    };

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    RestoreRecyclerItem getBackup(String[] files, String file) {
        RestoreRecyclerItem mBackup = new RestoreRecyclerItem("", "", "", "");

        String delims = "[_.]";
        String[] tokens = file.split(delims);
        if (tokens.length == 5) {

            if (tokens[0].equals("SMS")) {
                //Log.d("RestoreDialog: ", "tokens[0] = " + tokens[0]);
                return mBackup;
            } else {
                // Log.d("RestoreDialog: ", "tokens[0] = " + tokens[0]);
                delims = "[-]";
                String[] time = tokens[3].split(delims);
                String backupName = tokens[2] + " " + time[0] + ":" + time[1];
                mBackup.setName(backupName);
                mBackup.setUSSD_file_path(sdPath.toString() + File.separator + file);
                String json = loadJSONFromFile(sdPath.toString() + File.separator + file);

                try {
                    JSONObject obj = new JSONObject(json);
                    mBackup.setComment(obj.getString("comment"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    delims = "[_.]";
                    tokens = files[i].split(delims);
                    if (tokens[0].equals("SMS")) {
                        String StringForCompare = files[i].replace("SMS", "USSD");
                        if (file.equals(StringForCompare)) {
                            mBackup.setSMS_file_path(sdPath.toString() + File.separator + files[i]);
                        }
                    }
                }
            }
        }

        return mBackup;
    }

    public String loadJSONFromFile(String sourceFile) {
        String json = null;
        try {
            InputStream is = new FileInputStream(sourceFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    //Implement item click and long click over recycler view
    private void implementRecyclerViewClickListeners() {
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView,
                new RecyclerClick_Listener() {
            @Override
            public void onClick(View view, int position) {
                //If ActionMode not null select item
                if (mActionMode != null)
                    onListItemSelect(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                //Select item on long click
                onListItemSelect(position);
            }
        }));
    }

    //List item select method
    private void onListItemSelect(int position) {
        mAdapter.toggleSelection(position);//Toggle the selection

        boolean hasCheckedItems = mAdapter.getSelectedCount() > 0;//Check if any items are already selected or not


        if (hasCheckedItems && mActionMode == null) {
            // there are some selected items, start the actionMode
            mActionMode = startSupportActionMode(new Toolbar_ActionMode_Callback(this,
                    mAdapter, listItems, false));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                StatusbarColorAnimator anim = new StatusbarColorAnimator(this,
                        getResources().getColor(R.color.colorPrimaryDark),
                        getResources().getColor(R.color.select_mod_status_bar));
                anim.setDuration(250).start();
            }
        }
        else if (!hasCheckedItems && mActionMode != null) {
            // there no selected items, finish the actionMode
            mActionMode.finish();
        }

        if (mActionMode != null)
            //set action mode title on item selection
            mActionMode.setTitle(getString(R.string.restore_select_title)+ " " + String.valueOf(mAdapter
                    .getSelectedCount()));


    }

    public void setNullToActionMode() {
        if (mActionMode != null)
            mActionMode = null;
    }

    //Delete selected rows
    public void deleteRows() {
        SparseBooleanArray selected = mAdapter
                .getSelectedIds();//Get selected ids
        File sms_file;
        File ussd_file;

        //Loop all selected ids
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                //If current id is selected remove the item via key
                sms_file = new File(listItems.get(selected.keyAt(i)).getSMS_file_path());
                ussd_file = new File(listItems.get(selected.keyAt(i)).getUSSD_file_path());
                if (ussd_file.exists())
                    ussd_file.delete();
                if (sms_file.exists())
                    sms_file.delete();
//                deleteFile(listItems.get(selected.keyAt(i)).getSMS_file_path());
//                deleteFile(listItems.get(selected.keyAt(i)).getUSSD_file_path());
                listItems.remove(selected.keyAt(i));
                mAdapter.notifyDataSetChanged();//notify adapter

            }
        }
        //selected.size()
        Toast.makeText(this, getResources().getQuantityString(
                R.plurals.Deleted_backup_message, selected.size(), selected.size()), Toast.LENGTH_SHORT).show();//Show Toast
        mActionMode.finish();//Finish action mode after use

    }

    @Override
    public void processFinish(String backup_name) {
        String greetingText = String.format(getResources().getString(R.string.BackupRestoredMessage), backup_name);
        Toast.makeText(this, greetingText, Toast.LENGTH_LONG).show();


    }

    public void copyFileOrDirectory(String srcDir, String destDir){
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

    public void copyFile(File sourceFile, File destFile) throws IOException {
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

}
