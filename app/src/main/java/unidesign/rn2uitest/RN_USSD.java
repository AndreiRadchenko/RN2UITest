package unidesign.rn2uitest;

import android.content.Intent;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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
//import com.h6ah4i.android.example.advrecyclerview.R;

import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.util.ArrayList;
import java.util.List;

import unidesign.rn2uitest.helper.SimpleItemTouchHelperCallback;

public class RN_USSD extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final String LOG_TAG = "myLogs";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    //rrtyuuyty
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //FragmentPagerAdapter - detect a swipe or a tab click when user goes to a new tab
        //and expand appbar
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final AppBarLayout mAppBar = (AppBarLayout) findViewById(R.id.appbar);
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

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("intent.action.newussd");
                startActivity(intent);
/*              Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        //magicButton.getAnimationOnShow().setDuration(5000);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rn__ussd, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

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
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private ItemTouchHelper mItemTouchHelper;
        private RecyclerView recyclerView;
        private RecyclerView.LayoutManager mLayoutManager;
        private MyAdapter adapter;
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
//*****************************************************************************************************************************
//Заполнение фрагментов USSD и SMS
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_rn__ussd, container, false);

            recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
            mLayoutManager = new LinearLayoutManager(getContext());

            Log.d(LOG_TAG, "--- In OnCreateView() PlaceholderFragment ---");

            List<RecyclerItem> listItems = new ArrayList<>();
            //Generate sample data

            for (int k = 0; k<10; k++) {
                listItems.add(new RecyclerItem("Item " + (k), "Welcome to Torisan channel, this is description of item " + (k)));
            }

            //Set adapter
            final MyAdapter mAdapter = new MyAdapter(listItems, getActivity());
            adapter = mAdapter;

            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adapter);

            if (supportsViewElevation()) {
                // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
            } else {
                recyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.material_shadow_z1)));
            }
            recyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(getContext(), R.drawable.list_divider_h), true));

            //dragMgr.attachRecyclerView(recyclerView);
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(recyclerView);

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            Log.d(LOG_TAG, "--- In onResume() sectionNumber: ---" + sectionNumber);

        }

/*        @Override
        public void onPause() {
            dragMgr.cancelDrag();
            super.onPause();
        }

        @Override
        public void onDestroyView() {
            if (dragMgr != null) {
                dragMgr.release();
                dragMgr = null;
            }

            if (recyclerView != null) {
                recyclerView.setItemAnimator(null);
                recyclerView.setAdapter(null);
                recyclerView = null;
            }

            if (mWrappedAdapter != null) {
                WrapperAdapterUtils.releaseAll(mWrappedAdapter);
                mWrappedAdapter = null;
            }
            adapter = null;
            mLayoutManager = null;

            super.onDestroyView();
        }*/

        private boolean supportsViewElevation() {
            return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
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
}
