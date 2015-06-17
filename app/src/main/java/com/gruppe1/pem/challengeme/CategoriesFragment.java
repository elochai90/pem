package com.gruppe1.pem.challengeme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;


public class CategoriesFragment extends android.support.v4.app.Fragment {


    // TODO: make only one Instance in another file, to be able to access it from everywhere
    public static final String MY_PREFERENCES = "Preferences_File";
    public SharedPreferences.Editor editor;
    public SharedPreferences sharedPreferences;

    private AbsListView mListView;
    private ListAdapter mAdapter;

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        if(sharedPreferences.getBoolean("gridView", true))
            rootView = inflater.inflate(R.layout.fragment_categories_item_grid, container, false);
        else
            rootView = inflater.inflate(R.layout.fragment_categories_item_list, container, false);


        // TODO: replace by database categories
        ListItemIconName[] dummyCategoryItems = {
                new ListItemIconName(R.mipmap.test_tshirt, "Clothes"),
                new ListItemIconName(R.mipmap.test_tshirt, "Accessoires"),
                new ListItemIconName(R.mipmap.test_tshirt, "Shoes"),
                new ListItemIconName(R.mipmap.test_tshirt, "Bags"),
                new ListItemIconName(R.mipmap.test_tshirt, "Others")
        };

        if(sharedPreferences.getBoolean("gridView", true))
            mAdapter = new CategoriesItemAdapter(getActivity(), R.layout.grid_item_categories, dummyCategoryItems);
        else
            mAdapter = new CategoriesItemAdapter(getActivity(), R.layout.list_item_categories, dummyCategoryItems);

        // Set the adapter
        mListView = (AbsListView) rootView.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectCategory(position);
            }
        });
        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        DataBaseHelper db_helper = new DataBaseHelper(getActivity().getApplicationContext());
        db_helper.init();

        setHasOptionsMenu(true);

        // TEST
    /*
        Category testCategory1 = new Category(0, db_helper);
        testCategory1.setName("cat1");
        testCategory1.save();

        Category testCategory2 = new Category(0, db_helper);
        testCategory2.setName("cat2");
        testCategory2.save();

        Category testCategory3 = new Category(0, db_helper);
        testCategory3.setName("cat3");
        testCategory3.save();
    */

    }

    public void selectCategory(int id) {
        Intent intent = new Intent();
        intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".ItemsListActivity");
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.menu_categories_fragment, menu);
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

        return super.onOptionsItemSelected(item);
    }

}
