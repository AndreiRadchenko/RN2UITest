package unidesign.ussdsmscodes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.support.v4.app.LoaderManager;
import android.database.Cursor;

import android.content.Intent;

import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;
import unidesign.ussdsmscodes.IntroSlider.WelcomeActivity;
import unidesign.ussdsmscodes.MySQLight.TemplatesDataSource;
import unidesign.ussdsmscodes.MySQLight.USSDSQLiteHelper;
import unidesign.ussdsmscodes.Preferences.Pin_lock_activity;
import unidesign.ussdsmscodes.Preferences.SettingsPrefActivity;
import unidesign.ussdsmscodes.Preferences.pref_items;
import unidesign.ussdsmscodes.SettingsTools.BackupDialog;
import unidesign.ussdsmscodes.SettingsTools.BackupTask;
import unidesign.ussdsmscodes.SettingsTools.setupTemplateDialog;
import unidesign.ussdsmscodes.TempContentProvider.TempContentProvider;
import unidesign.ussdsmscodes.helper.SimpleItemTouchHelperCallback;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static unidesign.ussdsmscodes.Preferences.SettingsPrefActivity.PIN_REQUEST;
import static unidesign.ussdsmscodes.Preferences.pref_items.pref_Autorization;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class RN_USSD extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BackupDialog.NoticeDialogListener, setupTemplateDialog.mDialogListener {

    static final String LOG_TAG = "RN_USSD";
    static final String TAG = "Observer";
    static final int DISPOSE_OBSERVER = -100;
    static final int PERMISSION_REQUEST_CODE = 1;
    public static final int PERMISSION_READ_SD = 2;
    //public static Pin_lock_activity.CountThread PINCountThread = null;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
//    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private android.view.ActionMode actionMode;
    static Toolbar toolbar;
    static Toolbar select_toolbar;
    Toolbar select_toolbar_bottom;
    static AppBarLayout appbar;
    static CoordinatorLayout nested_CoordinatorLayout;
    AppBarLayout.LayoutParams scroll_params;
    FloatingActionButton fab;
    TabLayout tabLayout;
    NavigationView navigationView;
    DrawerLayout drawer;
    static MyAdapter currentTabAdapter;
    static MyAdapter USSDTabAdapter;
    private ActionMenuView amvMenu;
    static TextView select_toolbar_title;
    static View select_home;
    private View view;
    static FragmentManager mfragmentManager;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    public static unidesign.ussdsmscodes.IntroSlider.PreferenceManager prefManager;
    public static boolean pinCheckComplete = false;
    public static Handler h;
    public static final int TIMER_START = 1;
    public static final int TIMER_COUNT = 2;
    public static final int TIMER_STOP = 3;
    public static long mLockTime  = 0;
    //ImageView demo_item1;
    TextView  demo_item1, demo_item2;
    static RelativeLayout relativeLayout;
    public Animation enterAnimation, exitAnimation;

    public static final String ADMOB_APP_ID = "ca-app-pub-3260829463635761~1132663635";
    private AdView mAdView;
    public boolean select_all_checked = false;
    //public static int selected_items_count = 0;
    public static StaticCount myCount;
    public TourGuide mTutorialHandler;
    public static final int USSD_TAB = 0;
    public static final int SMS_TAB = 1;
    public static int current_tab = USSD_TAB;
    static AppBarLayout mAppBar;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    //rrtyuuyty
    public CustomViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = new unidesign.ussdsmscodes.IntroSlider.PreferenceManager(this);

        if(prefManager.isShowMainDemo()) {
        /* setup enter and exit animation for TourGuide*/
            enterAnimation = new AlphaAnimation(0f, 1f);
            enterAnimation.setDuration(600);
            enterAnimation.setFillAfter(true);

            exitAnimation = new AlphaAnimation(1f, 0f);
            exitAnimation.setDuration(600);
            exitAnimation.setFillAfter(true);

        /* initialize TourGuide without playOn() */
            mTutorialHandler = TourGuide.init(this).with(TourGuide.Technique.CLICK)
                    .setPointer(new Pointer()
                            .setGravity(Gravity.LEFT))
                    .setToolTip(new ToolTip()
                                    .setTitle(getString(R.string.demo_start_title))
                                    .setDescription("\n" + getString(R.string.demo_start_description))
                                    .setGravity(Gravity.BOTTOM|Gravity.RIGHT)
                                    //.setBackgroundColor(getResources().getColor(R.color.bg_slider_screen2))
                                    .setShadow(true)
                    )
                    .setOverlay(new Overlay()
                                    .setEnterAnimation(enterAnimation)
                                    .setExitAnimation(exitAnimation)
                                    .setBackgroundColor(R.color.holo_yellow_dark)
                    );
        }
        //mLockTime =
        sharedPrefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_main);

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, ADMOB_APP_ID);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mAdView.setVisibility(View.VISIBLE);
            }
        });
        mAdView.loadAd(adRequest);

        demo_item1 = findViewById(R.id.demo_item1);
        demo_item2 = findViewById(R.id.demo_item2);
        mfragmentManager = getSupportFragmentManager();
        //RxLifecycleAndroid.bindActivity(this);
//        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPrefs.edit();
        pinCheckComplete = false;

        // receiver for kill app when phone gonna idle mode (screen off)
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_REBOOT);
        filter.addAction(Intent.ACTION_SHUTDOWN);
        // ("kill.Requests.Notepad");
        registerReceiver(screenOffReceiver, filter);

        appbar = (AppBarLayout) findViewById(R.id.appbar);
        nested_CoordinatorLayout = (CoordinatorLayout) findViewById(R.id.nested_CoordinatorLayout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        relativeLayout = (RelativeLayout)  findViewById(R.id.dummy_relative);
        setSupportActionBar(toolbar);
        //scroll_params - for manipulate with scroll behawior
        scroll_params =
                (AppBarLayout.LayoutParams) relativeLayout.getLayoutParams();


        select_toolbar = (Toolbar) findViewById(R.id.select_toolbar);
        select_toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        select_toolbar.inflateMenu(R.menu.selected_menu);//changed
        //select_toolbar_bottom = (Toolbar) findViewById(R.id.select_toolbar_bottom);
        //amvMenu = (ActionMenuView) select_toolbar_bottom.findViewById(R.id.amvMenu);
        //getMenuInflater().inflate(R.menu.selected_menu_bottom, amvMenu.getMenu());
        myCount = new StaticCount();
        //select_toolbar_bottom.inflateMenu(R.menu.selected_menu_bottom);//changed
//=========================set normal mode (selection gone)========================================================
        select_home = (View) findViewById(R.id.select_home);
        select_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toolbar.getVisibility() == View.GONE)
                    setNormalMode();
                else
                    setSelectionMode();
            }
        });
//===================================================================================================
        //toolbar2 menu items CallBack listener
        select_toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem arg0) {
                switch (arg0.getItemId()) {
                    case R.id.action_select_all:
                        Log.d(LOG_TAG, "action_select_all");
                        switch (mViewPager.getCurrentItem()){
                            case 0:
                                if (RN_USSD.myCount.getCount() < USSDTabAdapter.getItemCount()) {
                                    Log.d(LOG_TAG, "action_select_ussd");
                                    USSDTabAdapter.selectAllItems();
                                    USSDTabAdapter.notifyDataSetChanged();
                                    //select_all_checked = true;
                                    RN_USSD.myCount.setCount(USSDTabAdapter.getItemCount());
                                }
                                else {
                                    USSDTabAdapter.deselectAllItems();
                                    USSDTabAdapter.notifyDataSetChanged();
                                    //select_all_checked = false;
                                    //RN_USSD.selected_items_count = 0;
                                    RN_USSD.myCount.setCount(0);
                                }
                                break;
                            case 1:
                                if (RN_USSD.myCount.getCount() < currentTabAdapter.getItemCount()) {
                                    Log.d(LOG_TAG, "action_select_sms");
                                    currentTabAdapter.selectAllItems();
                                    currentTabAdapter.notifyDataSetChanged();
                                    //select_all_checked = true;
                                    //RN_USSD.selected_items_count = currentTabAdapter.getItemCount();
                                    RN_USSD.myCount.setCount(currentTabAdapter.getItemCount());
                                }
                                else {
                                    currentTabAdapter.deselectAllItems();
                                    currentTabAdapter.notifyDataSetChanged();
                                    //select_all_checked = false;
                                    //RN_USSD.selected_items_count = 0;
                                    RN_USSD.myCount.setCount(0);
                                }
                                break;
                        }
                        //break;
                        return true;

                    case R.id.action_delete_selection:
                        // TODO: actually remove items
                        //Log.d(LOG_TAG, "action_delete_selection");
                        //Delete item
                        Uri uri = null;
                        switch (mViewPager.getCurrentItem()){
                            case 0:
                                for (int k = 0; k < USSDTabAdapter.getItemCount(); k++)
                                {
                                    RecyclerItem mRecycleItem = (RecyclerItem) USSDTabAdapter.listItems.get(k);
                                    if (mRecycleItem.isSelected()) {
                                        uri = Uri.parse(TempContentProvider.CONTENT_URI_USSD + "/"
                                                + mRecycleItem.getID());
                                        getContentResolver().delete(uri, null, null);
                                    }
                                }
                                USSDTabAdapter.notifyDataSetChanged();
                                //Log.d(LOG_TAG, "action_select_ussd");
                                break;
                            case 1:
                                for (int k = 0; k < currentTabAdapter.getItemCount(); k++)
                                {
                                    RecyclerItem mRecycleItem = (RecyclerItem) currentTabAdapter.listItems.get(k);
                                    if (mRecycleItem.isSelected()) {
                                        uri = Uri.parse(TempContentProvider.CONTENT_URI_SMS + "/"
                                                + mRecycleItem.getID());
                                        getContentResolver().delete(uri, null, null);
                                    }
                                }
                                currentTabAdapter.notifyDataSetChanged();
                                //Log.d(LOG_TAG, "action_select_ussd");
                                break;
                        }
//                        Snackbar.make(amvMenu.getRootView(), R.string.dele_items_snackbar, Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
                        Toast.makeText(getBaseContext(), getResources().getQuantityString(
                                R.plurals.Deleted_message, RN_USSD.myCount.getCount(), RN_USSD.myCount.getCount())
                                 , Toast.LENGTH_LONG).show();

                        setNormalMode();
                        return true;

                    default:
                        return false;
                }
            }
        });

        //toolbar2 menu items CallBack listener
//        amvMenu.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
//
//            @Override
//            public boolean onMenuItemClick(MenuItem arg0) {
//
//                switch (arg0.getItemId()) {
//                    case R.id.action_delete_selection:
//                        // TODO: actually remove items
//
//                        return true;
//
//                    default:
//                        return false;
//                }
//            }
//        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (CustomViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //FragmentPagerAdapter - detect a swipe or a tab click when user goes to a new tab
        //and expand appbar
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mAppBar = (AppBarLayout) findViewById(R.id.appbar);
        ActionBar actionBar = getActionBar();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mAppBar.setExpanded(true, true);
                fab.show();
                //fab.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
                //fab.setTranslationY(0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sectionNumber = mViewPager.getCurrentItem();
                switch (sectionNumber) {
                    case 0:
/*                        Snackbar.make(view, "FAB pressed in USSD fragment", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();*/
                        startActivity(new Intent("intent.action.newussd"));
                        break;
                    case 1:
/*                        Snackbar.make(view, "FAB pressed in SMS fragment", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();*/
                        startActivity(new Intent("intent.action.newsms"));
                        break;
                }
/*                Intent intent = new Intent("intent.action.newussd");
                startActivity(intent);*/
/*              Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(prefManager.isShowMainDemo()) {
                    mTutorialHandler.cleanUp();
                    mTutorialHandler
                            .setPointer(new Pointer())
                            .setToolTip(new ToolTip()
                                    .setTitle(getString(R.string.demo_one_title))
                                    .setDescription("\n" + getString(R.string.demo_one_description))
                                    .setGravity(Gravity.TOP | Gravity.RIGHT)
                                    //.setBackgroundColor(getResources().getColor(R.color.bg_slider_screen2))
                                    //.setTextColor(getResources().getColor(R.color.colorPrimaryDark))
                                    .setShadow(true)
                            )
                            .setOverlay(new Overlay()
                                    .setEnterAnimation(enterAnimation)
                                    .setExitAnimation(exitAnimation)
                                    .setBackgroundColor(R.color.holo_yellow_dark)
                            )
                            .playOn(demo_item2);
                }
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if(prefManager.isShowMainDemo())
                        mTutorialHandler.cleanUp();
            }
        };
        drawer.addDrawerListener(toggle);

        toggle.syncState();
        if(prefManager.isShowMainDemo())
            mTutorialHandler.playOn(demo_item1);

        h = new Handler() {
            @SuppressLint("NewApi")
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case TIMER_START:
                        pinCheckComplete = true;
                        // MainTabActivity.Timer.setText((CharSequence) msg.obj);
//                        MainTabActivity.SMS_tab_title
//                                .setText((CharSequence) msg.obj);
                        mLockTime = (long) msg.obj;
                        break;
                    case TIMER_COUNT:
                        // MainTabActivity.Timer.setText((CharSequence) msg.obj);
//                        MainTabActivity.SMS_tab_title
//                                .setText((CharSequence) msg.obj);
                        mLockTime = (long) msg.obj;
                        break;
                    case TIMER_STOP:
                        pinCheckComplete = false;

                        break;
                }
            };
        };

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            if (prefManager.isShowMainDemo() ){

            }
            else
                drawer.closeDrawer(GravityCompat.START);
            //mTutorialHandler.cleanUp();
        }
        else if (!toolbar.isShown()) {

            setNormalMode();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rn__ussd, menu);
        //getMenuInflater().inflate(R.menu.selected_menu_bottom, amvMenu.getMenu());
        return true;
    }

    @Override
    public void invalidateOptionsMenu(){


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//=========================set selection mode========================================================
        if (id == R.id.action_select) {
            //setSelectionMode();
            Log.d("R.id.action_intro", "intent.action.introslider");
            prefManager.setFirstTimeLaunch(true);
            prefManager.setShowMainDemo(true);
            startActivity(new Intent("intent.action.introslider"));
            finish();
                //getActionBar().hide();
                //actionMode = startActionMode(actionModeCallback);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
//===================================================================================================
//    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_import) {
            if (prefManager.isShowMainDemo())
                    mTutorialHandler.cleanUp();
            startActivity(new Intent("intent.action.import_templates"));

        }
        else if (id == R.id.nav_backup) {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED){

                SimpleDateFormat sdf = new SimpleDateFormat("_yy-MM-dd_HH-mm");
                String mydate = sdf.format(Calendar.getInstance().getTime());
                mydate = "_templates" + mydate;

                DialogFragment newFragment = new BackupDialog();
                Bundle args = new Bundle();
                args.putString("backup_name", mydate);
                newFragment.setArguments(args);
                newFragment.show(getSupportFragmentManager(), "backup_dialog");

/*                BackupTask AsyncBackup = new BackupTask(this);
                AsyncBackup.execute();*/
            }
            else {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
            }
        }

        else if (id == R.id.nav_restore) {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED) {

                startActivity(new Intent("intent.action.restore_templates"));
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_READ_SD);
            }
        }
        else if (id == R.id.nav_manage) {

            Intent settings_intent = new Intent(this, SettingsPrefActivity.class);
            startActivity(settings_intent);
            //startActivity(new Intent("intent.action.settings"));
        }
        else if (id == R.id.nav_share) {
            String appPackageName= getPackageName();
            String shareBody = "https://play.google.com/store/apps/details?id=" + appPackageName;

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_link_subject));

            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                    getString(R.string.share_link_text) + " " + shareBody);
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));

        }
        else if (id == R.id.nav_rate) {
            String appPackageName= getPackageName();
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+appPackageName));
            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET|
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(marketIntent);
            } catch (Exception e) {

            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
                                implements LoaderManager.LoaderCallbacks<Cursor> {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        TemplatesDataSource dbHelper;
        View rootView;
        public static final String ARG_SECTION_NUMBER = "section_number";
        private ItemTouchHelper mItemTouchHelper;
        private RecyclerView recyclerView;
        private RecyclerView.LayoutManager mLayoutManager;
        private MyAdapter adapter;
        //private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public int getSectionNumber() {

            return getArguments().getInt(ARG_SECTION_NUMBER);
        }
//*****************************************************************************************************************************
//Заполнение фрагментов USSD и SMS
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            rootView = inflater.inflate(R.layout.fragment_rn__ussd, container, false);
            recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
            mLayoutManager = new LinearLayoutManager(getContext());

            //Log.d(LOG_TAG, "--- In OnCreateView() PlaceholderFragment ---");
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            switch (sectionNumber) {
                case 1:
                    Log.d(LOG_TAG, "--- OnCreateView() PlaceholderFragment sectionNumber: ---" + sectionNumber);
                    getLoaderManager().initLoader(0, null, this);
                    //============================add ads here============================================

                    //====================================================================================
                    break;
                case 2:
                    Log.d(LOG_TAG, "--- OnCreateView() PlaceholderFragment sectionNumber: ---" + sectionNumber);
                    getLoaderManager().initLoader(1, null, this);
                    break;
            }

            final MyAdapter mAdapter = new MyAdapter(getActivity(), sectionNumber, new MyAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(RecyclerItem item, int mSecNumber) {
                    Log.d(LOG_TAG, "--- onItemClick in section --- " + mSecNumber);
                    if (!toolbar.isShown()) {

                    }
                    else if (item.getTemplate().contains("xx") || item.getTemplate().contains("XX") ||
                             item.getTemplate().contains("ХХ") || item.getTemplate().contains("хх") ||
                             item.getTemplate().contains("yy") || item.getTemplate().contains("YY") )
                    {
                        DialogFragment newFragment = new setupTemplateDialog();
                        Bundle args = new Bundle();
                        args.putLong("setup_id", item.getID());
                        args.putInt("setup_SecNumber", mSecNumber);
                        newFragment.setArguments(args);
                        newFragment.show(mfragmentManager, "setup_dialog");
                    }
                    else {
                        Intent intent;
                        switch (mSecNumber) {
                            case 1:
                                intent = new Intent(Intent.ACTION_DIAL);
                                //Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(ussdToCallableUri(item.getTemplate()));
                                try {
                                    startActivity(intent);
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case 2:

                                    Uri sms_uri = Uri.parse("smsto:" + item.getPhone());
                                    Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                                    sms_intent.putExtra("sms_body", item.getTemplate());
                                    startActivity(sms_intent);

                                break;
                        }
                    }
                    //Toast.makeText(getContext(), "Item Clicked in section ", Toast.LENGTH_LONG).show();
                }
            },
            new MyAdapter.OnItemLongClickListener(){
                @Override
                public void onItemLongClick(RecyclerItem item, int mSecNumber){
                    //select_home.setPressed(true);
                    select_home.performClick();
                    RN_USSD.myCount.setCount(RN_USSD.myCount.getCount() + 1);
                    String selected_items = getResources().getString(R.string.selection_toolbar_title_selected)
                            + " " + myCount.getCount();
                    select_toolbar_title.setText(selected_items);
                }
            }
            );
            adapter = mAdapter;

            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adapter);
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(recyclerView);
            adapter.setTouchHelper(mItemTouchHelper);

            if (sectionNumber == 1){
                USSDTabAdapter = adapter;
            }

            return rootView;
        }

        private Uri ussdToCallableUri(String ussd) {

            String uriString = "";
            if(!ussd.startsWith("tel:"))
                uriString += "tel:";

            for(char c : ussd.toCharArray()) {
                if(c == '#')
                    uriString += Uri.encode("#");
                else
                    uriString += c;
            }

            return Uri.parse(uriString);
        }

        @Override
        public void onPause(){
            super.onPause();
        // внесу изменения в базу данных произведенные в UI
        //    adapter.updateDB();
        }

        @Override
        public void onSaveInstanceState (Bundle outState){
            super.onSaveInstanceState(outState);
            // внесу изменения в базу данных произведенные в UI
            //adapter.updateDB();
        }

        @Override
        public void onResume() {
            super.onResume();
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            Log.d(LOG_TAG, "--- In onResume() sectionNumber: ---" + sectionNumber);
            //currentTabAdapter = adapter;
            currentTabAdapter = this.getAdapter();

//            mAppBar.setExpanded(false, true);
//            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());

            //recyclerView.smoothScrollToPosition(6);
            //getLoaderManager().initLoader(0, null, this);

        }

        MyAdapter getAdapter() {
            return adapter;
        }

        private boolean supportsViewElevation() {
            return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        }

        // Called when a previously created loader has finished loading
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            // Swap the new cursor in.  (The framework will take care of closing the
            // old cursor once we return.)
            switch (loader.getId()) {
                case 0:
                        Log.d(LOG_TAG, "--- In OnLoadFinished() PlaceholderFragmentUSSD ---");
                        adapter.swapCursorUSSD(data);
                    break;
                case 1:
                        Log.d(LOG_TAG, "--- In OnLoadFinished() PlaceholderFragmentSMS ---");
                        adapter.swapCursorSMS(data);
                    break;

            }
        }
        public void onLoaderReset(Loader<Cursor> loader) {
            // This is called when the last Cursor provided to onLoadFinished()
            // above is about to be closed.  We need to make sure we are no
            // longer using it.
            Log.d(LOG_TAG, "--- In OnLoaderReset() PlaceholderFragment ---");
            adapter.swapCursorUSSD(null);
            adapter.swapCursorSMS(null);
        }
        // creates a new loader after the initLoader () call
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader cursorLoader = null;
            switch (id) {
                case 0:
                    String[] projection0 = { USSDSQLiteHelper.COLUMN_ID, USSDSQLiteHelper.COLUMN_NAME,
                            USSDSQLiteHelper.COLUMN_COMMENT, USSDSQLiteHelper.COLUMN_TEMPLATE,
                            USSDSQLiteHelper.COLUMN_IMAGE};
                    cursorLoader = new CursorLoader(getContext(),
                            TempContentProvider.CONTENT_URI_USSD, projection0, null, null, null);
                    //cursorLoader = cursorLoader0;
                break;
                case 1:
                    String[] projection1 = { USSDSQLiteHelper.COLUMN_ID, USSDSQLiteHelper.COLUMN_NAME,
                                            USSDSQLiteHelper.COLUMN_COMMENT, USSDSQLiteHelper.COLUMN_PHONE_NUMBER,
                                            USSDSQLiteHelper.COLUMN_TEMPLATE, USSDSQLiteHelper.COLUMN_IMAGE};
                    cursorLoader = new CursorLoader(getContext(),
                            TempContentProvider.CONTENT_URI_SMS, projection1, null, null, null);
                    //return cursorLoader1;
                break;
            }
            return cursorLoader;
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "USSD";
                case 1:
                    return "SMS";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    void setSelectionMode(){

        appbar.setBackgroundColor(this.getResources().getColor(R.color.select_toolbar_background));
        //toolbar.setVisibility(toolbar.GONE);
        relativeLayout.setVisibility(toolbar.GONE);
        select_toolbar.setVisibility(select_toolbar.VISIBLE);

        select_toolbar.setAlpha(0);
        select_toolbar.animate().setDuration(500).alpha(1);

        scroll_params.setScrollFlags(0);
        fab.hide();
        mViewPager.disableScroll(true);

        Rect fabrect = new Rect();
        fab.getGlobalVisibleRect(fabrect);
        Rect apprect = new Rect();
        toolbar.getWindowVisibleDisplayFrame(apprect);
        if (!fabrect.intersect(apprect)){
            tabLayout.setVisibility(tabLayout.GONE);
        }
        else {
            tabLayout.setBackgroundColor(this.getResources().getColor(R.color.select_toolbar_background));
            LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
            for(int i = 0; i < tabStrip.getChildCount(); i++) {
                tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
            }
        }

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        currentTabAdapter.setMod(currentTabAdapter.SELECTION_MOD);
        USSDTabAdapter.setMod(USSDTabAdapter.SELECTION_MOD);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.select_mod_status_bar));
        }

        myCount.getCountChanges()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
        myCount.setCount(0);

    }

    void setNormalMode() {

        myCount.setCount(DISPOSE_OBSERVER);
        appbar.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
        //toolbar.setVisibility(toolbar.VISIBLE);
        relativeLayout.setVisibility(toolbar.VISIBLE);
        toolbar.setAlpha(0);
        toolbar.animate().setDuration(500).alpha(1);

        select_toolbar.setVisibility(select_toolbar.GONE);

        scroll_params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        fab.show();
        mViewPager.disableScroll(false);
        if (tabLayout.getVisibility() == View.GONE) {
            tabLayout.setVisibility(tabLayout.VISIBLE);
        }
        else {
            tabLayout.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
            LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
            for(int i = 0; i < tabStrip.getChildCount(); i++) {
                tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                       return false;

                    }
                });
            }
        }
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        currentTabAdapter.setMod(currentTabAdapter.NORMAL_MOD);
        USSDTabAdapter.setMod(USSDTabAdapter.NORMAL_MOD);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
           // window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }
        //RN_USSD.selected_items_count = 0;
    }

    final Observer<Integer> observer = new Observer<Integer>() {
        public Disposable d;

        @Override
        public void onSubscribe(Disposable d) {
            Log.e(TAG, "onSubscribe: " + Thread.currentThread().getName() + " "  + d.isDisposed());
            this.d = d;
        }

        @Override
        public void onNext(Integer count) {
            Log.e(TAG, "onNext: " + count + " " + Thread.currentThread().getName());
            if (count == 0) {
                //select_toolbar.setTitle(R.string.selection_toolbar_title);
                //select_toolbar_title.setText(R.string.selection_toolbar_title);
                setNormalMode();
            }
            else if (count == DISPOSE_OBSERVER){
                select_toolbar_title.setText(R.string.selection_toolbar_title);
                d.dispose();
                Log.e(TAG, "onNext: All Done!" + Thread.currentThread().getName() + " "  + d.isDisposed());
            }
            else {
                String selected_items = getResources().getString(R.string.selection_toolbar_title_selected)
                        + " " + count;
                select_toolbar_title.setText(selected_items);
            }

        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "onError: " + Thread.currentThread().getName());
        }

        @Override
        public void onComplete() {
            d.dispose();
            Log.e(TAG, "onComplete: All Done!" + Thread.currentThread().getName() + " "  + d.isDisposed());
            //d.dispose();
        }

    };

    @Override
    public void onResume() {
        //sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean PINCountAlive = false;
//        try {
//            PINCountAlive = Pin_lock_activity.PINCountThread.isAlive();
//        }
//        catch (NullPointerException e) {
//
//        }
        Date currDate = new Date(System.currentTimeMillis());
//        if (!PINCountAlive) {
            if (currDate.getTime() > mLockTime) {
            //PINCountThread = new Pin_lock_activity.CountThread();
            if (sharedPrefs.getBoolean(pref_items.pref_Autorization, false)) {
                Intent j = new Intent(this, Pin_lock_activity.class);
                j.putExtra("lanchMode", "checkin");
                startActivityForResult(j, PIN_REQUEST);
            }
        }
        mViewPager.setCurrentItem(current_tab);
        super.onResume();
    }

    @Override
    public void onPause(){
        current_tab = mViewPager.getCurrentItem();
        super.onPause();
        //pinCheckComplete = false;
    }

    @Override
    public void onStop(){
        super.onStop();
        //pinCheckComplete = false;
    }
    @Override
    public void onDestroy() {
        unregisterReceiver(screenOffReceiver);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                    Snackbar.make(tabLayout,"Permission Granted, Now you can access location data.",
//                            Snackbar.LENGTH_LONG).show();

                    new Timer().schedule(new TimerTask() {
                        @Override public void run() {
                            // EXECUTE ACTIONS (LIKE FRAGMENT TRANSACTION ETC.)
                            SimpleDateFormat sdf = new SimpleDateFormat("_yy-MM-dd_HH-mm");
                            String mydate = sdf.format(Calendar.getInstance().getTime());
                            mydate = "_templates" + mydate;

                            DialogFragment newFragment = new BackupDialog();
                            Bundle args = new Bundle();
                            args.putString("backup_name", mydate);
                            newFragment.setArguments(args);
                            newFragment.show(getSupportFragmentManager(), "backup_dialog");
                        }
                    }, 0);

                } else {

                    Snackbar.make(tabLayout, getResources().getString(R.string.ext_stor_perm_denied),
                            Snackbar.LENGTH_LONG).show();
                }
                break;
            case PERMISSION_READ_SD:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                    Snackbar.make(tabLayout,"Permission Granted, Now you can access location data.",
//                            Snackbar.LENGTH_LONG).show();
                    startActivity(new Intent("intent.action.restore_templates"));
                }
                else {
                    Snackbar.make(tabLayout, getResources().getString(R.string.ext_stor_perm_denied),
                            Snackbar.LENGTH_LONG).show();
                }
        }
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String name, String comment) {
        // User touched the dialog's positive button
        BackupTask AsyncBackup = new BackupTask(this);
        AsyncBackup.execute(name, comment);
    }

    @Override
    public void onSetupDialogPositiveClick(DialogFragment dialog, long id, int mSectionNumber) {
        // User touched the dialog's positive button
        Uri uri2edit = null;
        Intent intent = null;
        switch (mSectionNumber) {
            case 1:
                intent = new Intent("intent.action.editussd");
                uri2edit = Uri.parse(TempContentProvider.CONTENT_URI_USSD + "/"
                        + id);
                intent.putExtra(TempContentProvider.CONTENT_ITEM_TYPE_USSD, uri2edit);
                break;
            case 2:
                intent = new Intent("intent.action.editsms");
                uri2edit = Uri.parse(TempContentProvider.CONTENT_URI_SMS + "/"
                        + id);
                intent.putExtra(TempContentProvider.CONTENT_ITEM_TYPE_SMS, uri2edit);
                break;
        }
        startActivity(intent);

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String message = "";
        Bundle extras = data.getExtras();
        if (extras != null)
            message = extras.getString("message");
        // если пришло ОК
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PIN_REQUEST:
                    //pinCheckComplete = true;
                    break;
            }
        }
        // если вернулось не ОК
        else {
            switch (message){
                //onBackPressed() pin deleted
                case "":
                      finish();
//                    editor.putBoolean(pref_Autorization, authorization_switch_old_value);
//                    editor.commit();
//                    authorization_switch.setChecked(authorization_switch_old_value);
                    break;
                case "pin deleted":
//                    editor.putBoolean(pref_Autorization, false);
//                    editor.commit();
//                    authorization_switch.setChecked(false);
//                    Toast.makeText(mContext, "PIN set canceled", Toast.LENGTH_LONG).show();
                    break;
                case "delete app data":
                    editor.putBoolean(pref_Autorization, false);
                    editor.commit();
                    Toast.makeText(this, "Application data deleted", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    BroadcastReceiver screenOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "screenOffReceiver onReceive()");
            //finish();
        }
    };
}
