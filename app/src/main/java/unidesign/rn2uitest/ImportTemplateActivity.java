package unidesign.rn2uitest;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by United on 9/12/2017.
 */

public class ImportTemplateActivity extends AppCompatActivity implements ParseTask.AsyncResponse{

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

        try {
            URL templates_url = new URL(Import_Templates_URL);
            AsyncImport = new ParseTask(getApplicationContext(), this, templates_url);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        listItems.add(new ImportRecyclerItem("111", "template 111", "jsonref1", "pngref1", "USSD"));
//        listItems.add(new ImportRecyclerItem("222", "template 222", "jsonref2", "pngref2", "SMS"));

        AsyncImport.delegate = this;

        setContentView(R.layout.import_activity);

        recyclerView=(RecyclerView)findViewById(R.id.import_layout);

        mLayoutManager = new LinearLayoutManager(this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        AsyncImport.execute();
        //Log.d(LOG_TAG, "initializeData(): "+ listItems.get(0).getTemplatename());

        mAdapter = new ImportTemplateAdapter(listItems, new ImportTemplateAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ImportRecyclerItem item) {
                Log.d(LOG_TAG, "--- getJsondirref --- " + item.getJsondirref());
            }
        });


        recyclerView.setAdapter(mAdapter);
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
}
