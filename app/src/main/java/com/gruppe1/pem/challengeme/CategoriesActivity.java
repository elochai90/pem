package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;


public class CategoriesActivity extends Activity{


    // TODO: make only one Instance in another file, to be able to access it from everywhere
    public static final String MY_PREFERENCES = "Preferences_File";
    public SharedPreferences.Editor editor;
    public SharedPreferences sharedPreferences;

    private AbsListView mListView;
    private ListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(sharedPreferences.getBoolean("gridView", true))
            setContentView(R.layout.activity_categories_item_grid);
        else
            setContentView(R.layout.activity_categories_item_list);

        DataBaseHelper db_helper = new DataBaseHelper(getApplicationContext());
        db_helper.init();

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
        // TODO: replace by database categories
        ListItemIconName[] dummyCategoryItems = {
                new ListItemIconName(R.mipmap.test_tshirt, "Clothes"),
                new ListItemIconName(R.mipmap.test_tshirt, "Accessoires"),
                new ListItemIconName(R.mipmap.test_tshirt, "Shoes"),
                new ListItemIconName(R.mipmap.test_tshirt, "Bags"),
                new ListItemIconName(R.mipmap.test_tshirt, "Others")
        };

        if(sharedPreferences.getBoolean("gridView", true))
            mAdapter = new CategoriesItemAdapter(this, R.layout.grid_item_categories, dummyCategoryItems);
        else
            mAdapter = new CategoriesItemAdapter(this, R.layout.list_item_categories, dummyCategoryItems);

        // Set the adapter
        mListView = (AbsListView) findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectCategory(position);
            }
        });

    }

    public void selectCategory(int id) {
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), getPackageName() + ".ItemsListActivity");
        startActivity(intent);
    }

}
