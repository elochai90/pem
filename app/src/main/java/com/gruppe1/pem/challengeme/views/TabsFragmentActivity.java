package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.AnimatedTabHostListener;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.NavigationDrawerAdapter;
import com.gruppe1.pem.challengeme.helpers.Constants;

import java.util.Locale;

public class TabsFragmentActivity extends FragmentActivity {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private LocalActivityManager localActivityManager;
    private ListView mDrawerList;
    private FragmentTabHost tabs;
    private String mTitle;
    private String[] menuItems = {"Categories", "Compare", "Wishlist", "Settings"};
    private Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        menuItems[0] = getString(R.string.title_activity_categories);
        menuItems[1] = getString(R.string.title_activity_compare);
        menuItems[2] = getString(R.string.title_activity_wishlist2);
        menuItems[3] = getString(R.string.title_activity_settings);
        //menuItems =  {getString(R.string.title_activity_categories), ""+R.string.title_activity_compare, ""+R.string.title_activity_wishlist, ""+R.string.title_activity_settings};
        System.out.println(getString(R.string.title_activity_categories));

        mTitle = getString(R.string.app_name);
        if(getActionBar() != null) {
            getActionBar().setTitle(mTitle);
        }

        loadLocale();
        setupTabHost(savedInstanceState);
        setupNavigationDrawer();

    }

    public void loadLocale()
    {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFERENCES, Activity.MODE_PRIVATE);
        String language = prefs.getString(Constants.KEY_DS_4_NAME, "");
        System.out.println("laaang: " + language);
        String lang = "en";
        switch (language) {
            case "German":
                lang = "de";
                break;
            case "English":
                lang = "en";
                break;
            default:
                break;
        }
        changeLang(lang);
    }
    /* public void saveLocale(String lang)
    {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFERENCES, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }*/

    public void changeLang(String lang)
    {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        //saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (myLocale != null){
            newConfig.locale = myLocale;
            Locale.setDefault(myLocale);
            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onPostResume() {
        super.onResume();
        localActivityManager.dispatchResume();
        for (int i = 0; i < tabs.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) tabs.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#A4A4A4"));
            tv.setTextSize(14);
        }
        TextView tv = (TextView) tabs.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
        tv.setTextColor(Color.parseColor("#ffffff"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        localActivityManager.dispatchPause(isFinishing());
    }

    /**
     * selects the current tab in the navigation drawer
     */
    public void setSelectedNavigationDrawerItem() {
        mDrawerList.setItemChecked(tabs.getCurrentTab() + 1, true);
        mTitle = menuItems[tabs.getCurrentTab()];
        if(getActionBar() != null) {
            getActionBar().setTitle(mTitle);
        }
    }

    /**
     * sets up the navigation drawer
     */
    private void setupNavigationDrawer() {
        if(getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setClickable(true);
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mDrawerList.setTextFilterEnabled(true);
        ListItemIconName[] navigationDrawerItems = {
                new ListItemIconName(0, R.drawable.ic_categories, menuItems[0], null),
                new ListItemIconName(0, R.drawable.ic_compare, menuItems[1], null),
                new ListItemIconName(0, R.drawable.ic_wishlist, menuItems[2], null),
                new ListItemIconName(0, R.drawable.ic_settings, menuItems[3], null)
        };
        mDrawerList.setAdapter(new NavigationDrawerAdapter(this, R.layout.list_item_navigation_drawer, navigationDrawerItems));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position != 0)
                    mTitle = menuItems[position - 1];

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
                        Intent intent = new Intent();
                        intent.setClassName(getPackageName(), getPackageName() + ".views.SettingsActivity");
                        startActivity(intent);
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
        mDrawerList.setItemChecked(1, true);
    }

    /**
     * sets up the TabHost
     * @param savedInstanceState Bundle
     */
    private void setupTabHost(Bundle savedInstanceState) {
        localActivityManager = new LocalActivityManager(this, false);
        localActivityManager.dispatchCreate(savedInstanceState);

        tabs = (FragmentTabHost) findViewById(R.id.tabHost);
        tabs.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        tabs.addTab(tabs.newTabSpec(Constants.TAG_TAB_1).setIndicator(getString(R.string.title_activity_categories)),
                CategoriesFragment.class, null);
        tabs.addTab(tabs.newTabSpec(Constants.TAG_TAB_2).setIndicator(getString(R.string.title_activity_compare)),
                CompareFragment.class, null);
        tabs.addTab(tabs.newTabSpec(Constants.TAG_TAB_3).setIndicator(getString(R.string.title_activity_wishlist)),
                WishlistFragment.class, null);

        tabs.setOnTabChangedListener(new AnimatedTabHostListener(this, tabs));
        for (int i = 0; i < tabs.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) tabs.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#FFFFFF"));
            tv.setGravity(Gravity.CENTER);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Do you really want to exit '" + getResources().getString(R.string.app_name) + "'?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        TabsFragmentActivity.super.onBackPressed();
                    }
                }).create().show();
    }
}

