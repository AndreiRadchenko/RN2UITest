package unidesign.rn2uitest.SettingsTools;

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
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import unidesign.rn2uitest.R;

/**
 * Created by United on 12/26/2017.
 */

public class RestoreActivity extends AppCompatActivity {


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
        } else {
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

        mAdapter = new RestoreTemplateAdapter(listItems, new RestoreTemplateAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RestoreRecyclerItem item) {
                Log.d(LOG_TAG, "--- USSD file --- " + item.getUSSD_file_path());
                Log.d(LOG_TAG, "--- SMS file --- " + item.getSMS_file_path());
//                try {
//                    URL m_url = new URL(item.getJsondirref());
//                    AsyncImport = new ParseTask(getApplicationContext(), ITA, m_url);
//
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//                AsyncImport.execute(item.getName());
            }
        });

        RestoreSwipeAndDragHelper swipeAndDragHelper = new RestoreSwipeAndDragHelper(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeAndDragHelper);
        mAdapter.setTouchHelper(touchHelper);
        recyclerView.setAdapter(mAdapter);
        touchHelper.attachToRecyclerView(recyclerView);
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
        if (tokens.length > 1) {

            if (tokens[0].equals("SMS")) {
                Log.d("RestoreDialog: ", "tokens[0] = " + tokens[0]);
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
//                anim.addUpdateListener(anim);
                anim.setDuration(250).start();
                //ValueAnimator anim1 = ValueAnimator.ofFloat(0, 1);
                //Window  window = this.getWindow();
                //window.setStatusBarColor(this.getResources().getColor(R.color.select_mod_status_bar));
            }
        }
        else if (!hasCheckedItems && mActionMode != null) {
            // there no selected items, finish the actionMode
            mActionMode.finish();
        }

        if (mActionMode != null)
            //set action mode title on item selection
            mActionMode.setTitle(String.valueOf(mAdapter
                    .getSelectedCount()) + " selected");


    }

    public void setNullToActionMode() {
        if (mActionMode != null)
            mActionMode = null;
    }

    //Delete selected rows
    public void deleteRows() {
        SparseBooleanArray selected = mAdapter
                .getSelectedIds();//Get selected ids

        //Loop all selected ids
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                //If current id is selected remove the item via key
                listItems.remove(selected.keyAt(i));
                mAdapter.notifyDataSetChanged();//notify adapter

            }
        }
        Toast.makeText(this, selected.size() + " item deleted.", Toast.LENGTH_SHORT).show();//Show Toast
        mActionMode.finish();//Finish action mode after use

    }

}
