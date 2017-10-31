package unidesign.rn2uitest.ImportActivity;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

import unidesign.rn2uitest.ParseTask;
import unidesign.rn2uitest.R;

/**
 * Created by United on 9/12/2017.
 */

public class ImportTemplateActivity extends AppCompatActivity
        implements ParseTask.AsyncResponse
        {

    static final String LOG_TAG = "ImportTemplateActivity";
    static final String Import_Templates_URL = "https://drive.google.com/uc?export=download&confirm=no_antivirus&id=0B6DUrz2vzeEjUDlxTGRPUHZrRmc";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    public ImportTemplateAdapter mAdapter;
    ContentValues values = new ContentValues();
    public List<ImportRecyclerItem> listItems = new ArrayList<>();
    public ParseTask AsyncImport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_activity);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.import_toolbar);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        try {
            URL templates_url = new URL(Import_Templates_URL);
            AsyncImport = new ParseTask(getApplicationContext(), this, templates_url);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        listItems.add(new ImportRecyclerItem("111", "template 111", "jsonref1", "pngref1", "USSD"));
//        listItems.add(new ImportRecyclerItem("222", "template 222", "jsonref2", "pngref2", "SMS"));

//        AsyncImport.delegate = this;

       // setContentView(R.layout.import_activity);

        recyclerView=(RecyclerView)findViewById(R.id.import_layout);

        mLayoutManager = new LinearLayoutManager(this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        AsyncImport.execute("Templates preview");
        //Log.d(LOG_TAG, "initializeData(): "+ listItems.get(0).getTemplatename());
        final ImportTemplateActivity ITA = this;

        mAdapter = new ImportTemplateAdapter(listItems, new ImportTemplateAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ImportRecyclerItem item) {
                Log.d(LOG_TAG, "--- getJsondirref --- " + item.getJsondirref());
                Log.d(LOG_TAG, "--- getName() --- " + item.getName());
                try {
                    URL m_url = new URL(item.getJsondirref());
                    AsyncImport = new ParseTask(getApplicationContext(), ITA, m_url);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                AsyncImport.execute(item.getName());
                imageDownload(getApplicationContext(), item.getPngdirref(), item.getName());
            }
        });

        SwipeAndDragHelper swipeAndDragHelper = new SwipeAndDragHelper(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeAndDragHelper);
        mAdapter.setTouchHelper(touchHelper);
        recyclerView.setAdapter(mAdapter);
        touchHelper.attachToRecyclerView(recyclerView);

    };


    @Override
    public void processFinish(List<ImportRecyclerItem> output) {
        listItems.clear();
        listItems.addAll(output);
        mAdapter.notifyDataSetChanged();
        try {
            Log.d(LOG_TAG, "listItems.get(0).getTemplatename(): " + listItems.get(0).getTemplatename());
//        Log.d(LOG_TAG, "listItems.get(1).getTemplatename(): "+ listItems.get(1).getTemplatename());
        } catch (Exception e) {
            Toast.makeText(this, "Failed to download data: ", Toast.LENGTH_LONG).show();
        }

    }

            //save image
            public void imageDownload(Context ctx, String url, String img_name){
                    Picasso.with(ctx)
                            .load(url)
                            .into(getTarget(url, img_name));
            }

            //target to save
            private Target getTarget(final String url, final String img_name){
                Target target = new Target(){

                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                        new Thread(new Runnable() {

                            @Override
                            public void run() {

                                File sdPath = getFilesDir() ;
                                //File sdPath = Environment.getExternalStorageDirectory();
                                //sdPath = new File(sdPath.getPath() + "/" +
                                //        getResources().getString(R.string.app_name)  + "/" + "icons");
                                sdPath = new File(sdPath.getPath() + "/" + "icons");
                                if (!sdPath.exists()) {
                                    sdPath.mkdirs();
                                }

                                // ��������� ������ File, ������� �������� ���� � �����

                                String img_file = img_name + ".png";
                                File file = new File(sdPath, img_file);

                                try {
                                    file.createNewFile();
                                    FileOutputStream ostream = new FileOutputStream(file);
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 80, ostream);
                                    ostream.flush();
                                    ostream.close();
                                } catch (IOException e) {
                                    Log.e("IOException", e.getLocalizedMessage());
                                }
                            }
                        }).start();

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                return target;
            }

            /* Checks if external storage is available for read and write */
            public boolean isExternalStorageWritable() {
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    return true;
                }
                return false;
            }
}
