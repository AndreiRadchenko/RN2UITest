package unidesign.rn2uitest;

import android.app.ActionBar;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Telephony;
import android.support.design.widget.Snackbar;
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
import android.support.v7.view.ActionMode;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import org.reactivestreams.Subscription;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import unidesign.rn2uitest.MySQLight.TemplatesDataSource;
import unidesign.rn2uitest.MySQLight.USSDSQLiteHelper;
import unidesign.rn2uitest.TempContentProvider.TempContentProvider;
import unidesign.rn2uitest.helper.SimpleItemTouchHelperCallback;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;
import com.trello.rxlifecycle2.internal.Preconditions;

public class RN_USSD extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final String LOG_TAG = "myLogs";
    static final String TAG = "Observer";
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
    Toolbar select_toolbar;
    Toolbar select_toolbar_bottom;
    AppBarLayout appbar;
    AppBarLayout.LayoutParams scroll_params;
    FloatingActionButton fab;
    TabLayout tabLayout;
    NavigationView navigationView;
    DrawerLayout drawer;
    static MyAdapter currentTabAdapter;
    static MyAdapter USSDTabAdapter;
    private ActionMenuView amvMenu;
    TextView select_toolbar_title;
    Disposable disp;

    public boolean select_all_checked = false;
    //public static int selected_items_count = 0;
    public static StaticCount myCount;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    //rrtyuuyty
    public CustomViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //RxLifecycleAndroid.bindActivity(this);

        appbar = (AppBarLayout) findViewById(R.id.appbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //scroll_params - for manipulate with scroll behawior
        scroll_params =
                (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

        select_toolbar = (Toolbar) findViewById(R.id.select_toolbar);
        select_toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        select_toolbar.inflateMenu(R.menu.selected_menu);//changed
        select_toolbar_bottom = (Toolbar) findViewById(R.id.select_toolbar_bottom);
        amvMenu = (ActionMenuView) select_toolbar_bottom.findViewById(R.id.amvMenu);
        getMenuInflater().inflate(R.menu.selected_menu_bottom, amvMenu.getMenu());
        myCount = new StaticCount((TextView) select_toolbar_title);
        //select_toolbar_bottom.inflateMenu(R.menu.selected_menu_bottom);//changed
//=========================set normal mode (selection gone)========================================================
        View select_home = (View) findViewById(R.id.select_home);
        select_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNormalMode();
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
                                    if (USSDTabAdapter.listItems.get(k).isSelected()) {
                                        uri = Uri.parse(TempContentProvider.CONTENT_URI_USSD + "/"
                                                + USSDTabAdapter.listItems.get(k).getID());
                                        getContentResolver().delete(uri, null, null);
                                    }
                                }
                                USSDTabAdapter.notifyDataSetChanged();
                                //Log.d(LOG_TAG, "action_select_ussd");
                                break;
                            case 1:
                                for (int k = 0; k < currentTabAdapter.getItemCount(); k++)
                                {
                                    if (currentTabAdapter.listItems.get(k).isSelected()) {
                                        uri = Uri.parse(TempContentProvider.CONTENT_URI_SMS + "/"
                                                + currentTabAdapter.listItems.get(k).getID());
                                        getContentResolver().delete(uri, null, null);
                                    }
                                }
                                currentTabAdapter.notifyDataSetChanged();
                                //Log.d(LOG_TAG, "action_select_ussd");
                                break;
                        }
//                        Snackbar.make(amvMenu.getRootView(), R.string.dele_items_snackbar, Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
                        Toast.makeText(getBaseContext(), getString(R.string.Deleted) +
                                RN_USSD.myCount.getCount() + getString(R.string.items), Toast.LENGTH_SHORT).show();

                        setNormalMode();
                        return true;

                    default:
                        return false;
                }
            }
        });

        //toolbar2 menu items CallBack listener
        amvMenu.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem arg0) {

                switch (arg0.getItemId()) {
                    case R.id.action_delete_selection:
                        // TODO: actually remove items

                        return true;

                    default:
                        return false;
                }
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (CustomViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //FragmentPagerAdapter - detect a swipe or a tab click when user goes to a new tab
        //and expand appbar
        fab = (FloatingActionButton) findViewById(R.id.fab);
        final AppBarLayout mAppBar = (AppBarLayout) findViewById(R.id.appbar);
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

        //magicButton.getAnimationOnShow().setDuration(5000);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
            setSelectionMode();
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
            // Handle the import action
//            unidesign.rn2uitest.RN_USSD ma = this;
//            new ParseTask(getApplicationContext(), ma).execute();
            startActivity(new Intent("intent.action.import_templates"));

        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent("intent.action.gallery"));

        } else if (id == R.id.nav_slideshow) {



        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At least KitKat
                                {
                                    String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(getContext());
                                    Uri _uri = Uri.parse("tel:" + item.getPhone());
                                    intent = new Intent(Intent.ACTION_VIEW, _uri);
                                    intent.putExtra("address", item.getPhone());
                                    intent.putExtra("sms_body", item.getTemplate());
                                    intent.setPackage(defaultSmsPackageName);
                                    intent.setType("vnd.android-dir/mms-sms");
                                    startActivity(intent);
                                } else // For early versions, do what worked for you before.
                                {
                                    intent = new Intent(android.content.Intent.ACTION_VIEW);
                                    intent.setType("vnd.android-dir/mms-sms");
                                    intent.putExtra("address", item.getPhone());
                                    intent.putExtra("sms_body", item.getTemplate());
                                    startActivity(intent);
                                }
                                break;
                        }
                    }
                    //Toast.makeText(getContext(), "Item Clicked in section ", Toast.LENGTH_LONG).show();
                }
            });
            adapter = mAdapter;

            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adapter);
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(recyclerView);

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

        //select_all_checked = false;
        //RN_USSD.selected_items_count = 0;
//        myCount.getCountChanges()
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .subscribe(observer);
//        myCount.setCount(0);
//        myCount.getCountChanges()
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .subscribe( observer);
        myCount.setCount(0);
        disp = myCount.getCountChanges()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override public void accept(Integer s) {
                        //System.out.println(s);
                        Log.e(TAG, "onNext: " + s + " " + Thread.currentThread().getName());
                        if (s == 0)
                            //select_toolbar.setTitle(R.string.selection_toolbar_title);
                            select_toolbar_title.setText(R.string.selection_toolbar_title);
                        else {
                            String selected_items = getResources().getString(R.string.selection_toolbar_title_selected)
                                    + " " + s;
                            select_toolbar_title.setText(selected_items);
                        }
                    }
                });

        //myCount.setCount(0);
        //disp.dispose();

        toolbar.setVisibility(toolbar.GONE);
        select_toolbar.setVisibility(select_toolbar.VISIBLE);
       // select_toolbar_bottom.setVisibility(select_toolbar_bottom.VISIBLE);

        scroll_params.setScrollFlags(0);
        fab.hide();
        mViewPager.disableScroll(true);
        tabLayout.setVisibility(tabLayout.GONE);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        currentTabAdapter.setMod(currentTabAdapter.SELECTION_MOD);
        USSDTabAdapter.setMod(USSDTabAdapter.SELECTION_MOD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
           // window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.select_mod_status_bar));
        }

    }

    void setNormalMode() {
        //myCount.setCountComplete();
//        myCount.getCountChanges()
//                .unsubscribeOn(AndroidSchedulers.mainThread())
//                .subscribe(observer);;
        //myCount.getCountChanges().subscribe(null);
        myCount.setCount(0);
        disp.dispose();

        select_toolbar.setVisibility(select_toolbar.GONE);
        select_toolbar_bottom.setVisibility(select_toolbar_bottom.GONE);
        toolbar.setVisibility(toolbar.VISIBLE);
        scroll_params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        fab.show();
        mViewPager.disableScroll(false);
        tabLayout.setVisibility(tabLayout.VISIBLE);
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

    final Observer<StaticCount> observer = new Observer<StaticCount>() {
        public Disposable d;

        @Override
        public void onSubscribe(Disposable d) {
            Log.e(TAG, "onSubscribe: " + Thread.currentThread().getName());
            this.d = d;
        }

        @Override
        public void onNext(StaticCount count) {
            Log.e(TAG, "onNext: " + count.getCount() + " " + Thread.currentThread().getName());
            if (count.getCount() == 0)
                //select_toolbar.setTitle(R.string.selection_toolbar_title);
                select_toolbar_title.setText(R.string.selection_toolbar_title);
            else {
                String selected_items = getResources().getString(R.string.selection_toolbar_title_selected)
                        + " " + count.getCount();
                select_toolbar_title.setText(selected_items);
            }

        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "onError: " + Thread.currentThread().getName());
        }

        @Override
        public void onComplete() {
            Log.e(TAG, "onComplete: All Done!" + Thread.currentThread().getName());
            d.dispose();
        }

    };

    @Override
    public void onResume() {
        super.onResume();

    }

}
