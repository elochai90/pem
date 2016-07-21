package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.ItemsCollectionPagerAdapter;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.ImageDominantColorExtractor;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by bianka on 20.08.2015.
 */public class CollectionItemsActivity extends ActionBarActivity {

    ItemsCollectionPagerAdapter mItemsCollectionPagerAdapter;
    ViewPager mViewPager;

    private Locale myLocale;


    private int currentItemPosition;
    private ArrayList<ListItemIconName> mDataset = new ArrayList<>();
    private ArrayList<Item> itemCollection;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_items);

        loadLocale();
        setupToolbar();

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentItemPosition = extras.getInt(Constants.EXTRA_CLICKED_ITEM_POSITION);
            itemCollection = extras.getParcelableArrayList(Constants.EXTRA_ITEM_COLLECTION);
        }

        initDataset();

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        mItemsCollectionPagerAdapter =
                new ItemsCollectionPagerAdapter(
                        getSupportFragmentManager(), this, itemCollection);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mItemsCollectionPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentItemPosition = position;
                mItemsCollectionPagerAdapter.getItem(position);
//                getSupportActionBar().setTitle(mDataset.get(position).getName());
                getSupportActionBar().setTitle(mDataset.get(position).getName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(currentItemPosition);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;

            case R.id.action_item_save:
                saveItem((CollectionItemsFragment) mItemsCollectionPagerAdapter.getItem(currentItemPosition));
                setResult(RESULT_OK);
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * initializes the dataset of items
     */
    private void initDataset() {
        mDataset.clear();

        for (Item tmpItem : itemCollection) {
            int iconId = getResources().getIdentifier("kleiderbuegel", "drawable", "com.gruppe1.pem.challengeme");
            mDataset.add(new ListItemIconName("item", tmpItem.getId(), iconId, tmpItem.getName(), tmpItem.getImageFile()));
        }
    }

    private void saveItem(CollectionItemsFragment fragment) {
        fragment.saveItem();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                try {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    ((CollectionItemsFragment) mItemsCollectionPagerAdapter.getItem(currentItemPosition)).setBitmap(photo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                ((CollectionItemsFragment) mItemsCollectionPagerAdapter.getItem(currentItemPosition)).setImageUri(selectedImage);
            }
        }
    }

    public void loadLocale()
    {
        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFERENCES, Activity.MODE_PRIVATE);
        String language = prefs.getString(Constants.KEY_LANGUAGE, "");
        changeLang(language);
    }

    public void changeLang(String lang)
    {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
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

}

