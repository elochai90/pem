package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.ItemsCollectionPagerAdapter;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by bianka on 20.08.2015.
 */
public class CollectionItemsActivity extends ActionBarActivity {

   ItemsCollectionPagerAdapter mItemsCollectionPagerAdapter;

   @Bind (R.id.pager)
   ViewPager mViewPager;
   @Bind (R.id.toolbar)
   Toolbar toolbar;

   private Locale myLocale;
   private DataBaseHelper db_helper;

   private int currentItemPosition;
   private ArrayList<ListItemIconName> mDataset = new ArrayList<>();
   private ArrayList<Item> itemCollection;

   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_collection_items);
      ButterKnife.bind(this);

      db_helper = new DataBaseHelper(this);
      db_helper.init();

      loadLocale();
      setupToolbar();

      Bundle extras = getIntent().getExtras();
      // new item
      if (extras == null) {
         currentItemPosition = 0;
         itemCollection = new ArrayList<>();
         itemCollection.add(new Item(this, 0, getDb_helper()));
         if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_activity_new_item));
         }
      }
      // new item with parent category
      else if (extras.getInt(Constants.EXTRA_CATEGORY_ID, -1) != -1) {
         currentItemPosition = 0;
         itemCollection = new ArrayList<>();
         Item itemWithParent = new Item(this, 0, getDb_helper());
         itemWithParent.setCategoryId(extras.getInt(Constants.EXTRA_CATEGORY_ID));
         if (extras.getBoolean(Constants.EXTRA_ITEM_IS_WISHLIST)) {
            itemWithParent.setIsWish(1);
         }
         itemCollection.add(itemWithParent);
         if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_activity_new_item));
         }
      }
      // new wishlist item
      else if (extras.getBoolean(Constants.EXTRA_ITEM_IS_WISHLIST)) {
         currentItemPosition = 0;
         itemCollection = new ArrayList<>();
         Item wishlistItem = new Item(this, 0, getDb_helper());
         wishlistItem.setIsWish(1);
         itemCollection.add(wishlistItem);
         if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_activity_new_item));
         }
      }
      // edit item
      else {
         currentItemPosition = extras.getInt(Constants.EXTRA_CLICKED_ITEM_POSITION);
         itemCollection = extras.getParcelableArrayList(Constants.EXTRA_ITEM_COLLECTION);

      }
      initDataset();
      // ViewPager and its adapters use support library
      // fragments, so use getSupportFragmentManager.
      mItemsCollectionPagerAdapter =
            new ItemsCollectionPagerAdapter(getSupportFragmentManager(), this, itemCollection);
      mViewPager.setAdapter(mItemsCollectionPagerAdapter);
      mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
         @Override
         public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

         }

         @Override
         public void onPageSelected(int position) {
            currentItemPosition = position;
            mItemsCollectionPagerAdapter.getItem(position);
            if (getSupportActionBar() != null) {
               getSupportActionBar().setTitle(mDataset.get(position)
                     .getName());
            }
         }

         @Override
         public void onPageScrollStateChanged(int state) {

         }
      });
      mViewPager.setCurrentItem(currentItemPosition);
   }

   private void setupToolbar() {
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
            ((CollectionItemsFragment) mItemsCollectionPagerAdapter.getItem(
                  currentItemPosition)).submitForm();
      }
      return super.onOptionsItemSelected(item);
   }

   /**
    * initializes the dataset of items
    */
   private void initDataset() {
      mDataset.clear();

      for (Item tmpItem : itemCollection) {
         int iconId = getResources().getIdentifier("kleiderbuegel", "drawable",
               "com.gruppe1.pem.challengeme");
         mDataset.add(new ListItemIconName(this, "item", tmpItem.getId(), iconId, tmpItem.getName(),
               tmpItem.getImageFile()));
      }
   }

   public void loadLocale() {
      SharedPreferences prefs =
            getSharedPreferences(Constants.MY_PREFERENCES, Activity.MODE_PRIVATE);
      String language = prefs.getString(Constants.KEY_LANGUAGE, "");
      changeLang(language);
   }

   public void changeLang(String lang) {
      if (lang.equalsIgnoreCase("")) {
         return;
      }
      myLocale = new Locale(lang);
      //saveLocale(lang);
      Locale.setDefault(myLocale);
      android.content.res.Configuration config = getBaseContext().getResources()
            .getConfiguration();
      config.locale = myLocale;
      getBaseContext().getResources()
            .updateConfiguration(config, getBaseContext().getResources()
                  .getDisplayMetrics());
   }

   @Override
   public void onConfigurationChanged(android.content.res.Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      if (myLocale != null) {
         newConfig.locale = myLocale;
         Locale.setDefault(myLocale);
         getBaseContext().getResources()
               .updateConfiguration(newConfig, getBaseContext().getResources()
                     .getDisplayMetrics());
      }
   }

   /**
    * Get the db_helper instance for this class
    *
    * @return DataBaseHelper instance
    */
   private DataBaseHelper getDb_helper() {
      if (!db_helper.isOpen()) {
         System.out.println("db helper was closed");
         db_helper = new DataBaseHelper(this);
         db_helper.init();
      }
      return db_helper;
   }
}

