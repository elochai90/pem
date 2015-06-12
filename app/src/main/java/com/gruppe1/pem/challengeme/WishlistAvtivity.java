package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


public class WishlistAvtivity extends Activity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // specify an adapter (see also next example)
        // TODO: replace by database categories
        List<ListItemIconName> dummyCategoryItemsList = new ArrayList<ListItemIconName>();
        dummyCategoryItemsList.add(new ListItemIconName(R.mipmap.test_tshirt, "Clothes"));
        dummyCategoryItemsList.add(new ListItemIconName(R.mipmap.test_tshirt, "Accessoires"));
        dummyCategoryItemsList.add(new ListItemIconName(R.mipmap.test_tshirt, "Shoes"));
        dummyCategoryItemsList.add(new ListItemIconName(R.mipmap.test_tshirt, "Bags"));
        dummyCategoryItemsList.add(new ListItemIconName(R.mipmap.test_tshirt, "Others"));
        ListItemIconName[] dummyCategoryItems = {
                new ListItemIconName(R.mipmap.test_tshirt, "Clothes"),
                new ListItemIconName(R.mipmap.test_tshirt, "Accessoires"),
                new ListItemIconName(R.mipmap.test_tshirt, "Shoes"),
                new ListItemIconName(R.mipmap.test_tshirt, "Bags"),
                new ListItemIconName(R.mipmap.test_tshirt, "Others")
        };

        String[] dummyItems = {
            "Clothes", "Accessoires", "Shoes", "Bags", "Others"
        };

        mAdapter = new WishlistItemAdapter(dummyCategoryItemsList);
        mRecyclerView.setAdapter(mAdapter);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
