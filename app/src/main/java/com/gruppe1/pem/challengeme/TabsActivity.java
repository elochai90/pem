package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class TabsActivity extends Activity {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private LocalActivityManager mlam;
    private ListView mDrawerList;
    TabHost tabs;

    // TODO: make only one Instance in another file, to be able to access it from everywhere
    public static final String MY_PREFERENCES = "Preferences_File";

    private String mTitle;

    static String TAG_TAB_1 = "tag01";
    static String TAG_TAB_2 = "tag02";
    static String TAG_TAB_3 = "tag03";
    private  String[] menuItems = {"Categories", "Compare", "Wishlist", "Saved Compares", "Settings"};

    public void setSelectedNavigationDrawerItem() {
        mDrawerList.setItemChecked(tabs.getCurrentTab()+1, true);
        mTitle = menuItems[tabs.getCurrentTab()];
        getActionBar().setTitle(mTitle);
    }
/*

    private void setDefaultFont() {
        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/texgyreadventor-regular.otf");

            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField("SERIF");
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {
        }
    }
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_tabs);

        mTitle = getString(R.string.app_name);
        getActionBar().setTitle(mTitle);

        setupTabHost(savedInstanceState);
        setupNavigationDrawer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //menu.clear();
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_categories_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_settings:
                // TODO: openSettings();
                return true;
            case R.id.action_add:
                Toast.makeText(this, "Add gedrückt!", Toast.LENGTH_LONG).show();
                // TODO: not add, but change list/grid view
                System.out.println("Change grid view ");
                changeListGridView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // TODO: move global? or to preferences?
    private void changeListGridView() {
        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean gridView = sharedPreferences.getBoolean("gridView", true);
        gridView = !gridView;
        editor.putBoolean("gridView", gridView);
        editor.commit();
        System.out.println("GridView: " + gridView);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }


    @Override
    protected void onResume(){
        super.onResume();
        mlam.dispatchResume();
        for(int i=0;i<tabs.getTabWidget().getChildCount();i++)
        {
            TextView tv = (TextView) tabs.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#A4A4A4"));
            tv.setTextSize(14);
        }
        TextView tv = (TextView) tabs.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
        tv.setTextColor(Color.parseColor("#ffffff"));

    }

    @Override
    protected void onPause(){
        super.onPause();
        mlam.dispatchPause(isFinishing());
    }


    public  void setupNavigationDrawer() {
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setClickable(true);
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mDrawerList.setTextFilterEnabled(true);
        ListItemIconName[] navigationDrawerItems = {
                new ListItemIconName(android.R.drawable.ic_menu_agenda, menuItems[0]),
                new ListItemIconName(android.R.drawable.ic_menu_info_details, menuItems[1]),
                new ListItemIconName(android.R.drawable.ic_menu_sort_by_size, menuItems[2]),
                new ListItemIconName(android.R.drawable.ic_menu_save, menuItems[3]),
                new ListItemIconName(android.R.drawable.ic_menu_preferences, menuItems[4])
        };
        mDrawerList.setAdapter(new NavigationDrawerItemAdapter(this, R.layout.list_item_navigation_drawer, navigationDrawerItems));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // Getting an array of rivers
                //String[] menuItems = getResources().getStringArray(R.array.menus);
                Toast.makeText(getApplicationContext(), "Inside ClickListener...", Toast.LENGTH_SHORT).show();
                // Currently selected river
                if(position != 0)
                    mTitle = menuItems[position-1];

                switch (position) {
                    case 0:
                        // TODO: actualizeWeather();
                        break;
                    case 1:
                        tabs.setCurrentTab(0);
                        break;
                    case 2:
                        tabs.setCurrentTab(1);
                        break;
                    case 3:
                        tabs.setCurrentTab(2);
                        break;
                    case 4:
                        // TODO: to saved compares Fragment
                        tabs.setCurrentTab(2);
                        break;
                    case 5:
                        // TODO: Intent intent = new Intent(this, Preferences);
                        break;
                    default:
                }
                // Closing the drawer
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle("Menu");
                invalidateOptionsMenu();
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList.setItemChecked(1,true);
    }

    public void setupTabHost(Bundle savedInstanceState) {
        mlam = new LocalActivityManager(this, false);
        mlam.dispatchCreate(savedInstanceState);
        tabs=(TabHost)findViewById(R.id.tabHost);

        tabs.setup(mlam);

        TabHost.TabSpec spec = tabs.newTabSpec(TAG_TAB_1);
        Intent intent1 = new Intent(getApplicationContext(), CategoriesAvtivity.class);
        spec.setContent(intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        spec.setIndicator(getString(R.string.title_activity_categories));
        tabs.addTab(spec);

        spec = tabs.newTabSpec(TAG_TAB_2);
        Intent intent2 = new Intent(getApplicationContext(), CompareAvtivity.class);
        spec.setContent(intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        spec.setIndicator(getString(R.string.title_activity_compare));
        tabs.addTab(spec);

        spec = tabs.newTabSpec(TAG_TAB_3);
        Intent intent3 = new Intent(getApplicationContext(), WishlistAvtivity.class);
        spec.setContent(intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        spec.setIndicator(getString(R.string.title_activity_wishlist));
        tabs.addTab(spec);

        /*tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                System.out.println(tabs.getCurrentTab());
                mDrawerList.setItemChecked(tabs.getCurrentTab(), true);
            }
        });*/

        tabs.setOnTabChangedListener(new AnimatedTabHostListener(this, tabs));


        for(int i=0;i<tabs.getTabWidget().getChildCount();i++)
        {
            TextView tv = (TextView) tabs.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }
}
