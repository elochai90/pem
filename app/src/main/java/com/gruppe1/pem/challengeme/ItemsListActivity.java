package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;


public class ItemsListActivity extends Activity implements AbsListView.OnItemClickListener{
/*
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    */

    private AbsListView mListView;

    private ListAdapter mAdapter;


    // TODO: make only one Instance in another file, to be able to access it from everywhere
    public static final String MY_PREFERENCES = "Preferences_File";
    public SharedPreferences.Editor editor;
    public SharedPreferences sharedPreferences;


    /*// TODO: Rename and change types of parameters
    public static ItemsListActivity newInstance(String param1, String param2) {
        ItemsListActivity fragment = new ItemsListActivity();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_items_list);

        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(sharedPreferences.getBoolean("gridView", true))
           setContentView(R.layout.activity_categories_item_grid);
        else
            setContentView(R.layout.activity_categories_item_list);


        // TODO: replace by database categories
        ListItemIconName[] dummyItemItems = {
                new ListItemIconName(R.mipmap.test_tshirt, "T-Shirt rose"),
                new ListItemIconName(R.mipmap.test_tshirt, "T-Shirt rose"),
                new ListItemIconName(R.mipmap.test_tshirt, "T-Shirt rose"),
                new ListItemIconName(R.mipmap.test_tshirt, "T-Shirt rose"),
                new ListItemIconName(R.mipmap.test_tshirt, "T-Shirt rose")
        };

        if(sharedPreferences.getBoolean("gridView", true))
            mAdapter = new ItemItemAdapter(this, R.layout.grid_item_item, dummyItemItems);
        else
            mAdapter = new ItemItemAdapter(this, R.layout.list_item_item, dummyItemItems);

        // Set the adapter
        mListView = (AbsListView) findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items_list, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
    }

   // @Override
    public void selectItem(int id) {
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), getPackageName() + ".CategoriesItemDetailActivity");
        startActivity(intent);
    }

    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

}
