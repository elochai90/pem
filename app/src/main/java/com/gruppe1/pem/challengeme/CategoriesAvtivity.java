package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;


public class CategoriesAvtivity extends FragmentActivity implements CategoriesListFragment.OnFragmentInteractionListener, ItemsListFragment.OnFragmentInteractionListener {

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
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_categories_activity, menu);
        return true;
    }

    // TODO: move also global
    static void changeListGridView(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean gridView = sharedPreferences.getBoolean("gridView", true);
        gridView = !gridView;
        editor.putBoolean("gridView", gridView);
        editor.commit();
        System.out.println("GridView: " + gridView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings:
                // TODO: openSettings();
                return true;
            case R.id.action_add:
                // TODO: not add, but change list/grid view
                System.out.println("Change grid view ");
                changeListGridView(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void selectCategory(int id) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ItemsListFragment fragment = new ItemsListFragment();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void selectItem(int id) {
        System.out.println("Item " + id + " ausgewählt.");
    }
}
