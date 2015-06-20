package com.gruppe1.pem.challengeme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class CategoriesFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnLongClickListener{


    // TODO: make only one Instance in another file, to be able to access it from everywhere
    public static final String MY_PREFERENCES = "Preferences_File";
    public SharedPreferences.Editor editor;
    public SharedPreferences sharedPreferences;


    private static final String KEY_VIEW_AS_LIST = "ViewAsList";

    private List<ListItemIconName> mDataset;

    private View rootView;

    private GridView gridView;
    private DefaultGridAdapter gridAdapter;
    private ListView listView;
    private DefaultListAdapter listAdapter;
    private Boolean list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        initDataset();

        if (savedInstanceState != null) {
            list = savedInstanceState.getBoolean(KEY_VIEW_AS_LIST, true);
        } else {
            list = true;
        }

        rootView = getActivity().getLayoutInflater().inflate(R.layout.default_list_grid_view, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
        listAdapter = new DefaultListAdapter(getActivity(), R.layout.list_item_default, mDataset, false, false);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridAdapter = new DefaultGridAdapter(getActivity(), R.layout.grid_item_default, mDataset);
        gridView.setAdapter(gridAdapter);
        gridView.setVisibility(View.INVISIBLE);
        gridView.setOnItemClickListener(this);

        com.github.clans.fab.FloatingActionButton fab_add_wishlist_item = (FloatingActionButton) rootView.findViewById(R.id.add_wishlist_item);
        com.github.clans.fab.FloatingActionButton fab_add_category = (FloatingActionButton) rootView.findViewById(R.id.add_category);
        com.github.clans.fab.FloatingActionButton fab_add_item = (FloatingActionButton) rootView.findViewById(R.id.add_item);
        fab_add_wishlist_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".NewItemActivity");
                startActivity(intent);

            }
        });
        fab_add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".NewCategoryActivity");
                startActivity(intent);

            }
        });

        fab_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".NewItemActivity");
                startActivity(intent);

            }
        });



        sharedPreferences = getActivity().getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDataset();
        if (savedInstanceState != null) {
            list = savedInstanceState.getBoolean(KEY_VIEW_AS_LIST, true);
        } else {
            list = true;
        }

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

    public void selectCategory(View view) {
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

    private void switchListGridView(boolean shouldBeListView) {
        if(shouldBeListView) {
            gridView.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.INVISIBLE);
            gridView.setVisibility(View.VISIBLE);
        }

        list = shouldBeListView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_switchView) {
            switchListGridView(!list);
//            setRecyclerViewLayoutManager(!isViewAsList);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_VIEW_AS_LIST, list);
        super.onSaveInstanceState(savedInstanceState);
    }


    private void initDataset() {
        // TODO: replace by database data
        mDataset = new ArrayList<ListItemIconName>();
        mDataset.add(new ListItemIconName(0, "add new category"));
        mDataset.add(new ListItemIconName(R.drawable.hose, "Trousers"));
        mDataset.add(new ListItemIconName(R.drawable.mantel, "Coats"));
        mDataset.add(new ListItemIconName(R.drawable.schuh, "Shoes"));
        mDataset.add(new ListItemIconName(R.drawable.kleid, "Dresses"));
        mDataset.add(new ListItemIconName(R.drawable.tshirt, "T-Shirts"));
        mDataset.add(new ListItemIconName(R.drawable.cardigan, "Cardigans"));
    }

    private void addNewCategory(ListItemIconName newCat) {
        mDataset.add(newCat);
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0) {
            Intent intent = new Intent();
            intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".NewCategoryActivity");
            startActivity(intent);
        } else {
            selectCategory(view);
        }
    }
}
