package com.gruppe1.pem.challengeme.views;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.NavigationDrawerAdapter;
import com.gruppe1.pem.challengeme.helpers.Constants;

import java.util.Locale;

public class TabsFragmentActivity extends FragmentActivity  {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private FloatingActionMenu menu;

    private String mTitle;
    private String[] menuItems = new String[4];
    private Locale myLocale;

    private MainViewsPagerAdapter mainViewsPagerAdapter;
    private ViewPager mainViewsPager;
    private ActionBar.Tab tab0;
    private ActionBar.Tab tab1;
    private ActionBar.Tab tab2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        loadLocale();

        menuItems[0] = getString(R.string.title_activity_categories);
        menuItems[1] = getString(R.string.title_activity_compare);
        menuItems[2] = getString(R.string.title_activity_wishlist2);
        menuItems[3] = getString(R.string.title_activity_settings);

        setupNavigationDrawer();
        setupFloatingActionMenu();


        mTitle = getString(R.string.app_name);
        if(getActionBar() != null) {
            getActionBar().setTitle(mTitle);
        }

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mainViewsPager.setCurrentItem(tab.getPosition());
                setSelectedNavigationDrawerItem(tab.getPosition());
                mDrawerLayout.closeDrawer(mDrawerList);
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };


        mainViewsPagerAdapter = new MainViewsPagerAdapter(getSupportFragmentManager());
        mainViewsPager =(ViewPager) findViewById(R.id.pagerMainViews);
        mainViewsPager.setAdapter(mainViewsPagerAdapter);
        mainViewsPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        tab0 = actionBar.newTab()
                .setText(R.string.title_activity_categories)
                .setTabListener(tabListener);
        actionBar.addTab(tab0);

        tab1 = actionBar.newTab()
                .setText(R.string.title_activity_compare)
                .setTabListener(tabListener);
        actionBar.addTab(tab1);


        tab2 = actionBar.newTab()
                .setText(R.string.title_activity_wishlist)
                .setTabListener(tabListener);
        actionBar.addTab(tab2);


    }


    public void loadLocale()
    {
        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFERENCES, Activity.MODE_PRIVATE);
        String language = prefs.getString(Constants.KEY_LANGUAGE, "");

//        System.out.println("Load Language: " + Locale.getDefault());
        changeLang(language);
    }

    public void changeLang(String lang)
    {
        if (lang.equalsIgnoreCase("")) {
            myLocale = Locale.getDefault();
            String defaultLang = myLocale.getCountry().toLowerCase();
            if(!(defaultLang.equals("en") || defaultLang.equals("de"))) {
                defaultLang = "en";
            }
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.KEY_LANGUAGE, defaultLang);
            editor.apply();
        } else {
            myLocale = new Locale(lang);
        }
        //saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = getBaseContext().getResources().getConfiguration();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
        loadLocale();
    }

    @Override
    protected void onPostResume() {
        super.onResume();
        loadLocale();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLocale();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * selects the current tab in the navigation drawer
     */
    public void setSelectedNavigationDrawerItem(int tab_index) {
        mDrawerList.setItemChecked(tab_index + 1, true);
    }

    private void setupFloatingActionMenu() {

        menu = (FloatingActionMenu) findViewById(R.id.menu);
        menu.setClosedOnTouchOutside(true);
        com.github.clans.fab.FloatingActionButton fab_add_compare = (FloatingActionButton) findViewById(R.id.add_compare);
        fab_add_compare.setLabelText(getString(R.string.title_activity_saved_compares));
        com.github.clans.fab.FloatingActionButton fab_add_wishlist_item = (FloatingActionButton) findViewById(R.id.add_wishlist_item);
        fab_add_wishlist_item.setLabelText(getString(R.string.title_activity_new_Wishlistitem));
        com.github.clans.fab.FloatingActionButton fab_add_category = (FloatingActionButton) findViewById(R.id.add_category);
        fab_add_category.setLabelText(getString(R.string.title_activity_new_category));
        com.github.clans.fab.FloatingActionButton fab_add_item = (FloatingActionButton) findViewById(R.id.add_item);
        fab_add_item.setLabelText(getString(R.string.title_activity_new_item));
        fab_add_compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".views.NewCompareActivity");
                startActivity(intent);
                menu.close(false);

            }
        });

        fab_add_wishlist_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".views.NewItemActivity");
                intent.putExtra("is_wishlist", true);
                startActivityForResult(intent, 0);
                menu.close(false);

            }
        });
        fab_add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".views.NewCategoryActivity");
                startActivityForResult(intent, 0);
                menu.close(false);

            }
        });

        fab_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".views.NewItemActivity");
                startActivity(intent);
                menu.close(false);

            }
        });
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

                switch (position) {
                    case 0:
                        // TODO: actualizeWeather();
                        break;
                    case 1:
                        getActionBar().selectTab(tab0);
                        break;
                    case 2:
                        getActionBar().selectTab(tab1);
                        break;
                    case 3:
                        getActionBar().selectTab(tab2);
                        break;
                    case 4:
                        Intent intent = new Intent();
                        intent.setClassName(getPackageName(), getPackageName() + ".views.SettingsActivity");
                        startActivityForResult(intent, 0);
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
                getActionBar().setTitle(getString(R.string.nav_drawer_title));
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActionBar().setTitle(getString(R.string.app_name));
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList.setItemChecked(1, true);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.title_exit1) + " " + getResources().getString(R.string.app_name) + " " + getResources().getString(R.string.title_exit2))
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        TabsFragmentActivity.super.onBackPressed();
                    }
                }).create().show();
    }







    public static class MainViewsPagerAdapter extends FragmentPagerAdapter {
        public MainViewsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new CategoriesFragment();
                case 1:
                    return new CompareFragment();
                case 2:
                    return new WishlistFragment();
                default:
                    return new CategoriesFragment();
            }
        }


        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }
}




