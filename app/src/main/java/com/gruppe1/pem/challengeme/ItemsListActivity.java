package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ItemsListActivity extends Activity implements AdapterView.OnItemClickListener, View.OnLongClickListener{


    private List<ListItemIconName> mDataset;

    private GridView gridView;
    private DefaultGridAdapter gridAdapter;
    private ListView listView;
    private DefaultListAdapter listAdapter;
    private Boolean list;

    private int categoryId;

    public SharedPreferences.Editor editor;
    public SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        if (savedInstanceState != null) {
            list = savedInstanceState.getBoolean(Constants.KEY_VIEW_ITEMS_AS_LIST, true);
        } else {
            list = true;
        }
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                categoryId = -1;
            } else {
                categoryId = extras.getInt(Constants.EXTRA_CATEGORY_ID);
            }
        } else {
            categoryId = -1;
        }
        DataBaseHelper db_helper = new DataBaseHelper(this);
        db_helper.init();
        Category category = new Category(this, categoryId, db_helper);
        categoryId = category.getId();
        setTitle(category.getName());

        initDataset();

        setContentView(R.layout.default_list_grid_view);
        listView = (ListView) findViewById(R.id.listView);
        listAdapter = new DefaultListAdapter(this, R.layout.list_item_default, mDataset, false, false);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new DefaultGridAdapter(this, R.layout.grid_item_default, mDataset, false);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(this);
        gridView.setVisibility(View.INVISIBLE);

        com.github.clans.fab.FloatingActionButton fab_add_compare = (FloatingActionButton) findViewById(R.id.add_compare);
        com.github.clans.fab.FloatingActionButton fab_add_wishlist_item = (FloatingActionButton) findViewById(R.id.add_wishlist_item);
        com.github.clans.fab.FloatingActionButton fab_add_category = (FloatingActionButton) findViewById(R.id.add_category);
        com.github.clans.fab.FloatingActionButton fab_add_item = (FloatingActionButton) findViewById(R.id.add_item);
        fab_add_compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".NewCompareActivity");
                startActivity(intent);

            }
        });

        fab_add_wishlist_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".NewItemActivity");
                startActivity(intent);

            }
        });
        fab_add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".NewCategoryActivity");
                startActivity(intent);

            }
        });

        fab_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".NewItemActivity");
                startActivity(intent);

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        list = sharedPreferences.getBoolean(Constants.KEY_VIEW_ITEMS_AS_LIST, true);
        switchListGridView(list);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        list = sharedPreferences.getBoolean(Constants.KEY_VIEW_ITEMS_AS_LIST, true);
        menu.getItem(0).setIcon(list ? R.drawable.ic_view_grid : R.drawable.ic_view_list);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items_list, menu);
        return true;
    }

    private void switchListGridView(boolean shouldBeListView) {
        if(shouldBeListView) {
            gridView.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.INVISIBLE);
            gridView.setVisibility(View.VISIBLE);
        }

        list = shouldBeListView;
        editor.putBoolean(Constants.KEY_VIEW_ITEMS_AS_LIST, list);
        editor.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_switchView) {
            switchListGridView(!list);
            if(list) {
                item.setIcon(R.drawable.ic_view_grid);
            } else {
                item.setIcon(R.drawable.ic_view_list);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(Constants.KEY_VIEW_ITEMS_AS_LIST, list);
        super.onSaveInstanceState(savedInstanceState);
    }


    private void initDataset() {
        // TODO: replace by database data
        mDataset = new ArrayList<ListItemIconName>();

        DataBaseHelper db_helper = new DataBaseHelper(this);
        db_helper.init();

        mDataset = new ArrayList<ListItemIconName>();
        //mDataset.add(new ListItemIconName(0, 0, "add new category"));

        System.out.println("Category Id before init: " + categoryId);
//        DefaultSetup defaultSetup = new DefaultSetup(this);
//        defaultSetup.setup("setup_values.xml");
        ArrayList<Item> allCategoryItems = Item.getItemsByCategoryId(this, categoryId);
//        ArrayList<Item> allCategoryItems = Item.getAllItems(this);

        Iterator catIt = allCategoryItems.iterator();
        System.out.println("cat items: " + allCategoryItems.size());

        while (catIt.hasNext()) {
            Item tmpItem = (Item)catIt.next();
//            Log.e("###ITEM###", tmpItem.getName() + " - " + tmpItem.getId());
            int iconId = getResources().getIdentifier("kleiderbuegel", "drawable", "com.gruppe1.pem.challengeme"); // TODO: replace with image
            mDataset.add(new ListItemIconName(tmpItem.getId(), iconId , tmpItem.getName()));
        }
//        ArrayList<AttributeType> attributeTypes = AttributeType.getAttributeTypes(this);

    }

   // @Override
    public void selectItem(int itemid) {
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), getPackageName() + ".CategoriesItemDetailActivity");
        Bundle b = new Bundle();
        b.putInt(Constants.EXTRA_ITEM_ID, itemid);
        intent.putExtras(b);
        startActivity(intent);
    }


    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

      /*  if(position == 0) {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), getPackageName() + ".NewItemActivity");
            startActivity(intent);
        } else {*/
            int itemid = list ? listAdapter.getItem(position).elementId : gridAdapter.getItem(position).elementId;
            selectItem(itemid);
        //}
    }
}
