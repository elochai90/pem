package com.gruppe1.pem.challengeme;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class TabsActivity extends AppCompatActivity {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private LocalActivityManager mlam;
    private ListView mDrawerList;

    private String mTitle;

    static String TAG_TAB_1 = "tag01";
    static String TAG_TAB_2 = "tag02";
    static String TAG_TAB_3 = "tag03";
    private  String[] menuItems = {"Categories", "Compare", "Wishlist", "Saved Compares", "Settings"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        mTitle = getString(R.string.app_name);
        getSupportActionBar().setTitle(mTitle);

        mlam = new LocalActivityManager(this, false);
        mlam.dispatchCreate(savedInstanceState);
        final TabHost tabs=(TabHost)findViewById(R.id.tabHost);

        tabs.setup(mlam);

        TabHost.TabSpec spec = tabs.newTabSpec(TAG_TAB_1);
        Intent intent1 = new Intent(getApplicationContext(), CategoriesAvtivity.class);
        spec.setContent(intent1);
        spec.setIndicator(getString(R.string.title_activity_categories));
        tabs.addTab(spec);

        spec = tabs.newTabSpec(TAG_TAB_2);
        Intent intent2 = new Intent(getApplicationContext(), CompareAvtivity.class);
        spec.setContent(intent2);
        spec.setIndicator(getString(R.string.title_activity_compare));
        tabs.addTab(spec);

        spec = tabs.newTabSpec(TAG_TAB_3);
        Intent intent3 = new Intent(getApplicationContext(), WishlistAvtivity.class);
        spec.setContent(intent3);
        spec.setIndicator(getString(R.string.title_activity_wishlist));
        tabs.addTab(spec);

        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                System.out.println(tabs.getCurrentTab());
                mDrawerList.setItemChecked(tabs.getCurrentTab(), true);
            }
        });


        for(int i=0;i<tabs.getTabWidget().getChildCount();i++)
        {
            TextView tv = (TextView) tabs.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#FFFFFF"));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);



        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setClickable(true);
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mDrawerList.setTextFilterEnabled(true);
        NavigationDrawerItem [] navigationDrawerItems = {
                new NavigationDrawerItem(android.R.drawable.ic_menu_agenda, menuItems[0]),
                new NavigationDrawerItem(android.R.drawable.ic_menu_info_details, menuItems[1]),
                new NavigationDrawerItem(android.R.drawable.ic_menu_sort_by_size, menuItems[2]),
                new NavigationDrawerItem(android.R.drawable.ic_menu_save, menuItems[3]),
                new NavigationDrawerItem(android.R.drawable.ic_menu_preferences, menuItems[4])
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
                mTitle = menuItems[position];

                switch (position) {
                    case 0:
                        tabs.setCurrentTab(position);
                        break;
                    case 1:
                        tabs.setCurrentTab(position);
                        break;
                    case 2:
                        tabs.setCurrentTab(position);
                        break;
                    case 3:
                        tabs.setCurrentTab(1);
                        break;
                    case 4:
                        //Intent intent = new Intent(this, CompareAvtivity);
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
                getSupportActionBar().setTitle("Menu");
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_categories_activity, menu);
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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        return super.onOptionsItemSelected(item);
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
    }

    @Override
    protected void onPause(){
        super.onPause();
        mlam.dispatchPause(isFinishing());
    }
}
