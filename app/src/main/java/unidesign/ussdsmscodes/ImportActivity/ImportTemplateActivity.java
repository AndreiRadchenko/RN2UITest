package unidesign.ussdsmscodes.ImportActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;
import unidesign.ussdsmscodes.BuildConfig;
import unidesign.ussdsmscodes.IntroSlider.WelcomeActivity;
import unidesign.ussdsmscodes.Preferences.pref_items;
import unidesign.ussdsmscodes.R;
import unidesign.ussdsmscodes.RN_USSD;


/**
 * Created by United on 9/12/2017.
 */

public class   ImportTemplateActivity extends AppCompatActivity
        implements ParseTask.AsyncResponse
        {

    static final String LOG_TAG = "ImportTemplateActivity";
 // static final String Import_Templates_URL = "https://drive.google.com/uc?export=download&confirm=no_antivirus&id=0B6DUrz2vzeEjUDlxTGRPUHZrRmc"; //0B6DUrz2vzeEjUDlxTGRPUHZrRmc
    static final String Import_Templates_URL_ua = "https://drive.google.com/uc?export=download&confirm=no_antivirus&id=0B6DUrz2vzeEjUDlxTGRPUHZrRmc";
    static final String Import_Templates_URL_ru = "https://drive.google.com/uc?export=download&confirm=no_antivirus&id=1RUAKE8PKPPU4gq4JRljCqftudMZv4n89";
    static final String Import_Templates_URL_uz = "https://drive.google.com/uc?export=download&confirm=no_antivirus&id=1SfQLe-oQ89QRpvzHmPSDD0-TK3DGwLIb";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    public ImportTemplateAdapter mAdapter;
    ContentValues values = new ContentValues();
    public List<Object> listItems = new ArrayList<>();
    public ParseTask AsyncImport;
    public Animation enterAnimation, exitAnimation;
    public TourGuide mTutorialHandler;
    TextView demo_item3;
    URL templates_url;

    // A banner ad is placed in every 8th position in the RecyclerView.
    public static final int ITEMS_PER_AD = 15;
//    Test Banner ID
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";
//    Real Banner ID
//    private static final String AD_UNIT_ID = "ca-app-pub-3260829463635761/9200329067";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Use the chosen theme
        SharedPreferences sharedPrefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        boolean useDarkTheme = sharedPrefs.getBoolean(pref_items.pref_DarkTheme, false);

        if(useDarkTheme) {
            setTheme(R.style.Theme_Dark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_activity);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.import_toolbar);
        TextView invitation_txt = findViewById(R.id.invitation_txt);
        recyclerView=(RecyclerView)findViewById(R.id.import_layout);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        //ab.setDisplayShowHomeEnabled(true);

        TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();
        countryCodeValue = "ru";
        Log.d("ImportTemplateActivity", "TelephonyManager.getNetworkCountryIso(): " + countryCodeValue);

        try {
            switch (countryCodeValue) {
                case "ua":
                    templates_url = new URL(Import_Templates_URL_ua);
                    break;
                case "ru":
                    templates_url = new URL(Import_Templates_URL_ru);
                    break;
                case "uz":
                    templates_url = new URL(Import_Templates_URL_uz);
                    break;
                default:
                    if (RN_USSD.prefManager.isShowMainDemo() )
                        RN_USSD.prefManager.setShowMainDemo(false);
                    //Toast.makeText(this, "Sorry, I have no codes for you country", Toast.LENGTH_LONG).show();
                    recyclerView.setVisibility(View.INVISIBLE);
                    invitation_txt.setVisibility(View.VISIBLE);
                    return;
            }
//            URL templates_url = new URL(Import_Templates_URL);
            AsyncImport = new ParseTask(getApplicationContext(), this, templates_url);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        listItems.add(new ImportRecyclerItem("111", "template 111", "jsonref1", "pngref1", "USSD"));
//        listItems.add(new ImportRecyclerItem("222", "template 222", "jsonref2", "pngref2", "SMS"));

//        AsyncImport.delegate = this;

       // setContentView(R.layout.import_activity);

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
                //Log.d(LOG_TAG, "--- getJsondirref --- " + item.getJsondirref());
                //Log.d(LOG_TAG, "--- getName() --- " + item.getName());
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
            public boolean onOptionsItemSelected(MenuItem menuItem) {
                if (menuItem.getItemId() == android.R.id.home) {
                    onBackPressed();
                    return true;
                }
                return super.onOptionsItemSelected(menuItem);
            }

            @Override
            public void onBackPressed() {
                if (RN_USSD.prefManager.isShowMainDemo() ){

                    }
                else {
                    super.onBackPressed();
                }
            }

    @Override
    public void processFinish(List<Object> output) {

        listItems.clear();
        listItems.addAll(output);

        // add advertisment banners=================================================================
        addBannerAds();
        loadBannerAds();

        mAdapter.notifyDataSetChanged();
        try {
            ImportRecyclerItem recyclerItem = (ImportRecyclerItem) listItems.get(1);
            Log.d(LOG_TAG, "listItems.get(0).getTemplatename(): " + recyclerItem.getTemplatename());
//        Log.d(LOG_TAG, "listItems.get(1).getTemplatename(): "+ listItems.get(1).getTemplatename());
        } catch (Exception e) {
            Toast.makeText(this, "Failed to download data: ", Toast.LENGTH_LONG).show();
        }

        //Demo step two ==========================================================================
        if (RN_USSD.prefManager.isShowMainDemo() ) {
        /* setup enter and exit animation for TourGuide*/
            enterAnimation = new AlphaAnimation(0f, 1f);
            enterAnimation.setDuration(600);
            enterAnimation.setFillAfter(true);

            exitAnimation = new AlphaAnimation(1f, 0f);
            exitAnimation.setDuration(600);
            exitAnimation.setFillAfter(true);

        /* initialize TourGuide without playOn() */
            mTutorialHandler = TourGuide.init(this).with(TourGuide.Technique.CLICK)
                    .setPointer(null)
                    .setToolTip(new ToolTip()
                                    .setTitle(getString(R.string.demo_two_title))
                                    .setDescription(getString(R.string.demo_two_descriptin) + "\n")
                                    .setGravity(Gravity.TOP)
                                    //.setWidth(720)
                                    //.setBackgroundColor(getResources().getColor(R.color.bg_slider_screen2))
                                    //.setTextColor(getResources().getColor(R.color.colorPrimaryDark))
                                    .setShadow(true)
                            //.setBackgroundColor(getResources().getColor(R.color.bg_slider_screen3))
                    )
                    .setOverlay(new Overlay()
                            .setEnterAnimation(enterAnimation)
                            .setExitAnimation(exitAnimation)
                            .setBackgroundColor(R.color.holo_yellow_dark)
                            .disableClick(false)
                            .setStyle(Overlay.Style.NO_HOLE)
                            //.setBackgroundColor(getResources().getColor(R.color.overlay_transparent))
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mTutorialHandler.cleanUp();
                                    RN_USSD.prefManager.setShowMainDemo(false);
                                }
                            }));

            demo_item3 = findViewById(R.id.demo_item3);
            mTutorialHandler.playOn(demo_item3);
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

            //=========== ads in RecycleView methods=======================================================
            /**
             * Adds banner ads to the items list.
             */
            private void addBannerAds() {
                // Loop through the items array and place a new banner ad in every ith position in
                // the items List.
                //listItems = listItems;
                for (int i = 0; i <= listItems.size(); i += ITEMS_PER_AD) {
                    final AdView adView = new AdView(ImportTemplateActivity.this);
                    adView.setAdSize(AdSize.BANNER);
                    if (BuildConfig.BUILD_TYPE.contentEquals("release")) {
                        adView.setAdUnitId(RN_USSD.ADMOB_IMPORT_ACTIVITY_BANNER);
                    }
                    else {
                        adView.setAdUnitId(AD_UNIT_ID);
                    }
                    listItems.add(i, adView);
                    // templates.add(i, null);
                }
            }

            /**
             * Sets up and loads the banner ads.
             */
            private void loadBannerAds() {
                // Load the first banner ad in the items list (subsequent ads will be loaded automatically
                // in sequence).
                loadBannerAd(0);
            }

            /**
             * Loads the banner ads in the items list.
             */
            private void loadBannerAd(final int index) {

                if (index >= listItems.size()) {
                    return;
                }

                Object item = listItems.get(index);
                if (!(item instanceof AdView)) {
                    throw new ClassCastException("Expected item at index " + index + " to be a banner ad"
                            + " ad.");
                }

                final AdView adView = (AdView) item;

                // Set an AdListener on the AdView to wait for the previous banner ad
                // to finish loading before loading the next ad in the items list.
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        // The previous banner ad loaded successfully, call this method again to
                        // load the next ad in the items list.
                        loadBannerAd(index + ITEMS_PER_AD);
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // The previous banner ad failed to load. Call this method again to load
                        // the next ad in the items list.
                        Log.e("MainActivity", "The previous banner ad failed to load. Attempting to"
                                + " load the next banner ad in the items list.");
                        loadBannerAd(index + ITEMS_PER_AD);
                    }
                });

                // Load the banner ad.
                //adView.loadAd(new AdRequest.Builder().addTestDevice("B5F1C5518A85DFF1729ED74BEC3F62D7").build());
                adView.loadAd(new AdRequest.Builder().build());
            }

        }
