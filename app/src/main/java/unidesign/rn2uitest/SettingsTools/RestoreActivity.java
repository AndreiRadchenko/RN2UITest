package unidesign.rn2uitest.SettingsTools;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import unidesign.rn2uitest.ImportActivity.SwipeAndDragHelper;
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

        File sdPath = new File(Environment.getExternalStorageDirectory()
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
                mBackup.setUSSD_file_path(file);

                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    delims = "[_.]";
                    tokens = files[i].split(delims);
                    if (tokens[0].equals("SMS")) {
                        String StringForCompare = files[i].replace("SMS", "USSD");
                        if (file.equals(StringForCompare)) {
                            mBackup.setSMS_file_path(files[i]);
                        }
                    }
                }
            }
        }

        return mBackup;
    }

}
