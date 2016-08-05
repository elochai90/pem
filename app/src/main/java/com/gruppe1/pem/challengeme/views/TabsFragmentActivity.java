package com.gruppe1.pem.challengeme.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.ViewPagerAdapter;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DefaultSetup;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TabsFragmentActivity extends ActionBarActivity {

   private String mTitle;
   private Locale myLocale;

   @Bind(R.id.appBar)
   AppBarLayout appBarLayout;
   @Bind(R.id.pagerMainViews)
   ViewPager mainViewsPager;
   @Bind(R.id.tabLayout)
   TabLayout tabLayout;
   @Bind(R.id.toolbar)
   Toolbar toolbar;
   @Bind(R.id.menu)
   FloatingActionMenu menu;
   @Bind(R.id.add_item)
   FloatingActionButton fab_add_item;
   @Bind(R.id.add_category)
   FloatingActionButton fab_add_category;
   @Bind(R.id.add_wishlist_item)
   FloatingActionButton fab_add_wishlist_item;
   @Bind(R.id.add_compare)
   FloatingActionButton fab_add_compare;

   private ViewPagerAdapter viewPagerAdapter;


   private void setupToolbar() {
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayShowHomeEnabled(false);
      getSupportActionBar().setHomeButtonEnabled(false);
      getSupportActionBar().setDisplayHomeAsUpEnabled(false);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.activity_tabs);
      ButterKnife.bind(this);
      loadLocale();
      checkFirstDBInit();

      setupToolbar();

      setupFloatingActionMenu();

      mTitle = getString(R.string.app_name);

      setupViewPager(mainViewsPager);
      tabLayout.setupWithViewPager(mainViewsPager);

      if (getSupportActionBar() != null) {
         getSupportActionBar().setTitle(mTitle);
      }

      // Create a tab listener that is called when the user changes tabs.
      tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
         @Override
         public void onTabSelected(TabLayout.Tab tab) {
            onTabClickedSwitched(tab);
         }

         @Override
         public void onTabUnselected(TabLayout.Tab tab) {

         }

         @Override
         public void onTabReselected(TabLayout.Tab tab) {

         }
      });

      mainViewsPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
         @Override
         public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
         }

         @Override
         public void onPageSelected(int position) {
            onTabClickedSwitched(tabLayout.getTabAt(position));
         }

         @Override
         public void onPageScrollStateChanged(int state) {

         }
      });
   }

   private void checkFirstDBInit() {
      SharedPreferences sharedPreferences =
            getSharedPreferences(Constants.MY_PREFERENCES, Activity.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPreferences.edit();
      boolean firstDbInit = sharedPreferences.getBoolean(Constants.KEY_FIRST_DB_INIT, true);

      if (firstDbInit) {
         new DefaultSetup(getApplicationContext());
         editor.putBoolean(Constants.KEY_FIRST_DB_INIT, false);
         editor.commit();
      }
   }

   private void onTabClickedSwitched(TabLayout.Tab tab) {

      mainViewsPager.setCurrentItem(tab.getPosition());
      expandToolbar();
   }

   public void expandToolbar() {
      CoordinatorLayout.LayoutParams params =
            (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
      AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
      if (behavior != null) {
         behavior.setTopAndBottomOffset(0);
         behavior.onNestedPreScroll((CoordinatorLayout) findViewById(R.id.coordinatorLayout),
               appBarLayout, null, 0, 1, new int[2]);
      }
   }

   private void setupViewPager(ViewPager viewPager) {
      viewPagerAdapter = new ViewPagerAdapter(this, getSupportFragmentManager());
      viewPager.setAdapter(viewPagerAdapter);
      viewPagerAdapter.notifyDataSetChanged();

   }

   public void loadLocale() {
      SharedPreferences prefs =
            getSharedPreferences(Constants.MY_PREFERENCES, Activity.MODE_PRIVATE);
      String language = prefs.getString(Constants.KEY_LANGUAGE, "");
      changeLang(language);
   }

   public void changeLang(String lang) {
      if (lang.equalsIgnoreCase("")) {
         myLocale = Locale.getDefault();
         String defaultLang = myLocale.getCountry()
               .toLowerCase();
         if (!(defaultLang.equals("en") || defaultLang.equals("de"))) {
            defaultLang = "en";
         }
         SharedPreferences sharedPreferences =
               getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
         SharedPreferences.Editor editor = sharedPreferences.edit();
         editor.putString(Constants.KEY_LANGUAGE, defaultLang);
         editor.apply();
      } else {
         myLocale = new Locale(lang);
      }
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

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      return super.onOptionsItemSelected(item);
   }

   @Override
   protected void onPostCreate(Bundle savedInstanceState) {
      super.onPostCreate(savedInstanceState);
      loadLocale();
   }

   @Override
   protected void onPostResume() {
      super.onPostResume();
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

   private void setupFloatingActionMenu() {
      menu.setClosedOnTouchOutside(true);
      fab_add_compare.setLabelText(getString(R.string.title_activity_saved_outfits));
      fab_add_wishlist_item.setLabelText(getString(R.string.title_activity_new_Wishlistitem));
      fab_add_category.setLabelText(getString(R.string.title_activity_new_category));
      fab_add_item.setLabelText(getString(R.string.title_activity_new_item));
      fab_add_compare.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), getPackageName() + ".views.NewCompareActivity");
            startActivityForResult(intent, 0);
            menu.close(false);
         }
      });

      fab_add_wishlist_item.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(),
                  getPackageName() + ".views.CollectionItemsActivity");
            intent.putExtra(Constants.EXTRA_ITEM_IS_WISHLIST, true);
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
            intent.setClassName(getPackageName(),
                  getPackageName() + ".views.CollectionItemsActivity");
            startActivityForResult(intent, 0);
            menu.close(false);
         }
      });
   }

   @Override
   public void onBackPressed() {
      new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.title_exit1) + " " +
            getResources().getString(R.string.app_name) + " " +
            getResources().getString(R.string.title_exit2))
            .setNegativeButton(android.R.string.no, null)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

               public void onClick(DialogInterface arg0, int arg1) {
                  TabsFragmentActivity.super.onBackPressed();
               }
            })
            .create()
            .show();
   }

   // for actualizing the categories list on coming back from new category
   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (viewPagerAdapter.getItem(0).getActivity() != null) {
         viewPagerAdapter.getItem(0).onActivityResult(requestCode, resultCode, data);
      }
      if (viewPagerAdapter.getItem(1).getActivity() != null) {
         viewPagerAdapter.getItem(1).onActivityResult(requestCode, resultCode, data);
      }
//      if (wishlistFragment.getActivity() != null) {
         viewPagerAdapter.getItem(2).onActivityResult(requestCode, resultCode, data);
//      }
   }
}




