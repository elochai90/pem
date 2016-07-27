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

import java.util.Locale;

public class TabsFragmentActivity extends ActionBarActivity {

   private FloatingActionMenu menu;

   private String mTitle;
   private Locale myLocale;

   private ViewPager mainViewsPager;
   private TabLayout tabLayout;

   private CategoriesFragment categoriesFragment;
   private CompareFragment compareFragment;
   private WishlistFragment wishlistFragment;
   private AppBarLayout appBarLayout;
   private Toolbar toolbar;

   private void setupToolbar() {
      toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayShowHomeEnabled(false);
      getSupportActionBar().setHomeButtonEnabled(false);
      getSupportActionBar().setDisplayHomeAsUpEnabled(false);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.activity_tabs);
      loadLocale();

      appBarLayout = (AppBarLayout) findViewById(R.id.appBar);

      setupToolbar();

      setupFloatingActionMenu();

      mTitle = getString(R.string.app_name);

      categoriesFragment = new CategoriesFragment();
      compareFragment = new CompareFragment();
      wishlistFragment = new WishlistFragment();

      mainViewsPager = (ViewPager) findViewById(R.id.pagerMainViews);
      setupViewPager(mainViewsPager);
      tabLayout = (TabLayout) findViewById(R.id.tabLayout);
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
      ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
      adapter.addFrag(categoriesFragment, getString(R.string.title_activity_categories));
      adapter.addFrag(compareFragment, getString(R.string.title_activity_compare));
      adapter.addFrag(wishlistFragment, getString(R.string.title_activity_wishlist));
      viewPager.setAdapter(adapter);
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

      menu = (FloatingActionMenu) findViewById(R.id.menu);
      menu.setClosedOnTouchOutside(true);
      com.github.clans.fab.FloatingActionButton fab_add_compare =
            (FloatingActionButton) findViewById(R.id.add_compare);
      fab_add_compare.setLabelText(getString(R.string.title_activity_saved_compares));
      com.github.clans.fab.FloatingActionButton fab_add_wishlist_item =
            (FloatingActionButton) findViewById(R.id.add_wishlist_item);
      fab_add_wishlist_item.setLabelText(getString(R.string.title_activity_new_Wishlistitem));
      com.github.clans.fab.FloatingActionButton fab_add_category =
            (FloatingActionButton) findViewById(R.id.add_category);
      fab_add_category.setLabelText(getString(R.string.title_activity_new_category));
      com.github.clans.fab.FloatingActionButton fab_add_item =
            (FloatingActionButton) findViewById(R.id.add_item);
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
      if (categoriesFragment.getActivity() != null) {
         categoriesFragment.onActivityResult(requestCode, resultCode, data);
      }
      if (compareFragment.getActivity() != null) {
         compareFragment.onActivityResult(requestCode, resultCode, data);
      }
      if (wishlistFragment.getActivity() != null) {
         wishlistFragment.onActivityResult(requestCode, resultCode, data);
      }
   }
}




