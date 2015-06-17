package com.gruppe1.pem.challengeme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;


public class CategoriesFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener{


    // TODO: make only one Instance in another file, to be able to access it from everywhere
    public static final String MY_PREFERENCES = "Preferences_File";
    public SharedPreferences.Editor editor;
    public SharedPreferences sharedPreferences;


    private static final String KEY_VIEW_AS_LIST = "ViewAsList";

    private boolean isViewAsList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ListItemIconName> mDataset;

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        rootView = inflater.inflate(R.layout.default_recycler_view, container, false);


        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        isViewAsList = true;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            isViewAsList = savedInstanceState.getBoolean(KEY_VIEW_AS_LIST, true);
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new DefaultItemAdapter(getActivity(),mDataset,isViewAsList, this, this);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDataset();

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

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param shouldBeViewAsList if the view should be shown as list or as grid
     */
    public void setRecyclerViewLayoutManager(boolean shouldBeViewAsList) {
        int scrollPosition = 0;

        isViewAsList = shouldBeViewAsList;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        if(!isViewAsList) {
            mLayoutManager = new GridLayoutManager(getActivity(), 3);
        } else {
            mLayoutManager = new LinearLayoutManager(getActivity());
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
        mAdapter.notifyDataSetChanged();
        ((DefaultItemAdapter) mAdapter).updateView(isViewAsList);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_switchView) {
            setRecyclerViewLayoutManager(!isViewAsList);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_VIEW_AS_LIST, isViewAsList);
        super.onSaveInstanceState(savedInstanceState);
    }


    private void initDataset() {
        // TODO: replace by database data
        mDataset = new ArrayList<ListItemIconName>();
        mDataset.add(new ListItemIconName(R.mipmap.test_tshirt, "Clothes"));
        mDataset.add(new ListItemIconName(R.mipmap.test_tshirt, "Accessoires"));
        mDataset.add(new ListItemIconName(R.mipmap.test_tshirt, "Shoes"));
        mDataset.add(new ListItemIconName(R.mipmap.test_tshirt, "Bags"));
        mDataset.add(new ListItemIconName(R.mipmap.test_tshirt, "Others"));
    }

    @Override
    public void onClick(View v) {
        selectCategory(v);
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

}
