package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Interpolator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class CategoriesFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnLongClickListener{
    public static final int REQUEST_CODE = 1;

    public SharedPreferences.Editor editor;
    public SharedPreferences sharedPreferences;

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
            list = savedInstanceState.getBoolean(Constants.KEY_VIEW_CATEGORIES_AS_LIST, true);
        } else {
            list = true;
        }

        rootView = getActivity().getLayoutInflater().inflate(R.layout.default_list_grid_view, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
        listAdapter = new DefaultListAdapter(getActivity(), R.layout.list_item_default, mDataset, false);
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


        sharedPreferences = getActivity().getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initDataset();
        if (savedInstanceState != null) {
            list = savedInstanceState.getBoolean(Constants.KEY_VIEW_CATEGORIES_AS_LIST, true);
        } else {
            list = true;
        }


        setHasOptionsMenu(true);


    }

    public void selectCategory(int categoryId) {
        Intent intent = new Intent();
        intent.setClassName(getActivity().getPackageName(), getActivity().getPackageName() + ".ItemsListActivity");
        Bundle b = new Bundle();
        b.putInt(Constants.EXTRA_CATEGORY_ID, categoryId);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        list = sharedPreferences.getBoolean(Constants.KEY_VIEW_CATEGORIES_AS_LIST, true);
        switchListGridView(list);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        list = sharedPreferences.getBoolean(Constants.KEY_VIEW_CATEGORIES_AS_LIST, true);
        menu.getItem(0).setIcon(list ? R.drawable.ic_view_grid : R.drawable.ic_view_list);
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
        editor.putBoolean(Constants.KEY_VIEW_CATEGORIES_AS_LIST, list);
        editor.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
        savedInstanceState.putSerializable(Constants.KEY_VIEW_CATEGORIES_AS_LIST, list);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void initDataset() {
        // TODO: replace by database data
        // TEST
        DataBaseHelper db_helper = new DataBaseHelper(getActivity().getApplicationContext());
        db_helper.init();
//        Category testCategory1 = new Category(0, db_helper);
//        testCategory1.setName("Trousers");
//        testCategory1.save();
//
//        Category testCategory2 = new Category(0, db_helper);
//        testCategory2.setName("Coats");
//        testCategory2.save();
//
//        Category testCategory3 = new Category(0, db_helper);
//        testCategory3.setName("Shoes");
//        testCategory3.save();
//
//        Category testCategory4 = new Category(0, db_helper);
//        testCategory4.setName("Dresses");
//        testCategory4.save();
//
//        Category testCategory5 = new Category(0, db_helper);
//        testCategory5.setName("T-Shirts");
//        testCategory5.save();
//
//        Category testCategory6 = new Category(0, db_helper);
//        testCategory6.setName("Cardigans");
//        testCategory6.save();
//
//        Category testCategory7 = new Category(0, db_helper);
//        testCategory7.setName("Anzug");
//        testCategory7.save();
//
//        Category testCategory8 = new Category(0, db_helper);
//        testCategory8.setName("Bademode");
//        testCategory8.save();
//
//        Category testCategory9 = new Category(0, db_helper);
//        testCategory9.setName("Jacke");
//        testCategory9.save();
//
//        Category testCategory10 = new Category(0, db_helper);
//        testCategory10.setName("Rock");
//        testCategory10.save();
//
//        Category testCategory11 = new Category(0, db_helper);
//        testCategory11.setName("Pulli");
//        testCategory11.save();
//
//        Category testCategory12 = new Category(0, db_helper);
//        testCategory12.setName("Shorts");
//        testCategory12.save();
//
//        Category testCategory13 = new Category(0, db_helper);
//        testCategory13.setName("Sneaker");
//        testCategory13.save();
//
//        Category testCategory14 = new Category(0, db_helper);
//        testCategory14.setName("Socken");
//        testCategory14.save();
//
//        Category testCategory15 = new Category(0, db_helper);
//        testCategory15.setName("Stiefel");
//        testCategory15.save();
//
//        Category testCategory16 = new Category(0, db_helper);
//        testCategory16.setName("Unterwaesche");
//        testCategory16.save();
//
//        Category testCategory17 = new Category(0, db_helper);
//        testCategory17.setName("Tops");
//        testCategory17.save();

        mDataset = new ArrayList<ListItemIconName>();
        mDataset.add(new ListItemIconName(0, 0, "add new category"));

        DefaultSetup defaultSetup = new DefaultSetup(getActivity().getApplicationContext());
        defaultSetup.setup("setup_values.xml");
        ArrayList<Category> allCategories = Category.getAllCategories(getActivity().getApplicationContext());

        Iterator catIt = allCategories.iterator();

        while (catIt.hasNext()) {
            Category tmpCat = (Category)catIt.next();
            Log.e("###ITEM CAT###", tmpCat.getName() + " - " + tmpCat.getId());
            int iconId = getResources().getIdentifier(tmpCat.getIcon(), "drawable", "com.gruppe1.pem.challengeme");
            mDataset.add(new ListItemIconName(tmpCat.getId(), iconId , tmpCat.getName()));
        }

//        mDataset.add(new ListItemIconName( testCategory1.getId(), R.drawable.hose, testCategory1.getName()));
//        mDataset.add(new ListItemIconName( testCategory2.getId(), R.drawable.mantel, testCategory2.getName()));
//        mDataset.add(new ListItemIconName( testCategory3.getId(), R.drawable.schuh, testCategory3.getName()));
//        mDataset.add(new ListItemIconName(testCategory4.getId(), R.drawable.kleid, testCategory4.getName()));
//        mDataset.add(new ListItemIconName(testCategory5.getId(), R.drawable.tshirt, testCategory5.getName()));
//        mDataset.add(new ListItemIconName(testCategory6.getId(), R.drawable.cardigan, testCategory6.getName()));
//        mDataset.add(new ListItemIconName( testCategory7.getId(), R.drawable.hose, testCategory1.getName()));
//        mDataset.add(new ListItemIconName( testCategory8.getId(), R.drawable.mantel, testCategory2.getName()));
//        mDataset.add(new ListItemIconName( testCategory9.getId(), R.drawable.schuh, testCategory3.getName()));
//        mDataset.add(new ListItemIconName(testCategory10.getId(), R.drawable.kleid, testCategory4.getName()));
//        mDataset.add(new ListItemIconName(testCategory11.getId(), R.drawable.tshirt, testCategory5.getName()));
//        mDataset.add(new ListItemIconName(testCategory12.getId(), R.drawable.cardigan, testCategory6.getName()));
//        mDataset.add(new ListItemIconName(testCategory13.getId(), R.drawable.kleid, testCategory4.getName()));
//        mDataset.add(new ListItemIconName(testCategory14.getId(), R.drawable.tshirt, testCategory14.getName()));
//        mDataset.add(new ListItemIconName(testCategory15.getId(), R.drawable.cardigan, testCategory15.getName()));
//        mDataset.add(new ListItemIconName(testCategory16.getId(), R.drawable.kleid, testCategory16.getName()));
//        mDataset.add(new ListItemIconName(testCategory17.getId(), R.drawable.tshirt, testCategory17.getName()));

        ArrayList<AttributeType> attributeTypes = AttributeType.getAttributeTypes(getActivity().getApplicationContext());
        Item.getItemsByCategoryId(getActivity().getApplicationContext(), 1);
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
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            selectCategory(mDataset.get(position).elementId);
        }
    }

    // for actualizing the categories list on coming back from new category
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CODE) {
                if(resultCode == Activity.RESULT_OK) {
                    System.out.println("Result ok");
                    String categoryName = data.getStringExtra("categoryName");
                    int categoryId = data.getIntExtra("categoryId", -1);
                    int categoryParentId = data.getIntExtra("categoryParentId", -1);
//                    if (categoryParentId == this.category) TODO: check if category is the same like parentCategory of new Category
                    mDataset.add(new ListItemIconName(categoryId, R.drawable.kleiderbuegel, categoryName));
                    listAdapter = new DefaultListAdapter(getActivity(), R.layout.list_item_default, mDataset, false);
                    listView.setAdapter(listAdapter);
                    listAdapter.notifyDataSetChanged();
                    gridAdapter = new DefaultGridAdapter(getActivity(), R.layout.grid_item_default, mDataset);
                    gridView.setAdapter(gridAdapter);
                    gridAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception ex) {
            Toast.makeText(getActivity(), ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

    }
}
