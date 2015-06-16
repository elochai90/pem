package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;


public class WishlistActivity extends Activity {

    private static final String KEY_VIEW_AS_LIST = "ViewAsList";

    private boolean isViewAsList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ListItemIconName> mDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        initDataset();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        isViewAsList = true;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            isViewAsList = savedInstanceState.getBoolean(KEY_VIEW_AS_LIST, true);
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new WishlistItemAdapter(this,mDataset,isViewAsList);
        mRecyclerView.setAdapter(mAdapter);


        RadioButton mLinearLayoutRadioButton = (RadioButton) findViewById(R.id.linear_layout_rb);
        mLinearLayoutRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);
                setRecyclerViewLayoutManager(true);
            }
        });

        RadioButton mGridLayoutRadioButton = (RadioButton) findViewById(R.id.grid_layout_rb);
        mGridLayoutRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecyclerViewLayoutManager(false);
            }
        });

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
            mLayoutManager = new GridLayoutManager(this, 3);
        } else {
            mLayoutManager = new LinearLayoutManager(this);
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
        mAdapter.notifyDataSetChanged();
        ((WishlistItemAdapter) mAdapter).updateView(isViewAsList);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wishlist_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
        mDataset.add(new ListItemIconName(R.mipmap.test_tshirt, "T-Shirt in rosa"));
        mDataset.add(new ListItemIconName(R.mipmap.test_tshirt, "T-Shirt in rosa"));
        mDataset.add(new ListItemIconName(R.mipmap.test_tshirt, "T-Shirt in rosa"));
        mDataset.add(new ListItemIconName(R.mipmap.test_tshirt, "T-Shirt in rosa"));
        mDataset.add(new ListItemIconName(R.mipmap.test_tshirt, "T-Shirt in rosa"));


    }
}
