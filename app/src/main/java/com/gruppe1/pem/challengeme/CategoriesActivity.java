package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


public class CategoriesActivity extends FragmentActivity implements CategoriesListFragment.OnFragmentInteractionListener, ItemsListFragment.OnFragmentInteractionListener {

    private FragmentManager fragmentManager;


    // TODO: make only one Instance in another file, to be able to access it from everywhere
    public static final String MY_PREFERENCES = "Preferences_File";
    public SharedPreferences.Editor editor;
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);


        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CategoriesListFragment fragment = new CategoriesListFragment();
        fragmentTransaction.add(R.id.fragment_container, fragment, "categories");
        fragmentTransaction.commit();

        DataBaseHelper db_helper = new DataBaseHelper(getApplicationContext());
        db_helper.init();

        // TEST
        Category testCategory = new Category(1, db_helper);
    }



    @Override
    public void selectCategory(int id) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ItemsListFragment fragment = new ItemsListFragment();
        fragmentTransaction.replace(R.id.fragment_container, fragment, "categories_items");
        fragmentTransaction.commit();
    }

    @Override
    public void selectItem(int id) {
        System.out.println("Item " + id + " ausgewählt.");
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CategoriesItemDetailFragment fragment = new CategoriesItemDetailFragment();
        fragmentTransaction.replace(R.id.fragment_container, fragment, "categories_items_detail");
        fragmentTransaction.commit();
    }

    public void callActivityMethod(Intent intent, ImageView vw, int requestCode){
        TabsActivity tabsActivity = (TabsActivity) getParent();
        tabsActivity.callAction(intent, vw, requestCode);
    }

}
