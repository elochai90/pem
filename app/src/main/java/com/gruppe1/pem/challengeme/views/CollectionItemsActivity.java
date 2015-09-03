package com.gruppe1.pem.challengeme.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.adapters.ItemsCollectionPagerAdapter;
import com.gruppe1.pem.challengeme.helpers.ImageDominantColorExtractor;

import java.util.ArrayList;

/**
 * Created by bianka on 20.08.2015.
 */public class CollectionItemsActivity extends FragmentActivity {

    ItemsCollectionPagerAdapter mItemsCollectionPagerAdapter;
    ViewPager mViewPager;


    private int currentItemPosition;
    private int categoryId;
    private ArrayList<ListItemIconName> mDataset = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_items);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            categoryId = extras.getInt("EXTRA_CATEGORY_ID");
            currentItemPosition = extras.getInt("EXTRA_CLICKED_ITEM_POSITION");
        }

        initDataset();

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        mItemsCollectionPagerAdapter =
                new ItemsCollectionPagerAdapter(
                        getSupportFragmentManager(), this, categoryId);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mItemsCollectionPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentItemPosition = position;
                mItemsCollectionPagerAdapter.getItem(position);
                getActionBar().setTitle(getResources().getString(R.string.edit) + ": " + mDataset.get(position).getName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(currentItemPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;

            case R.id.action_item_save:
                saveItem((CollectionItemsFragment) mItemsCollectionPagerAdapter.getItem(currentItemPosition));
                setResult(RESULT_OK);
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * initializes the dataset of compares
     */
    private void initDataset() {
        mDataset.clear();
        ArrayList<Item> allCategoryItems = Item.getItemsByCategoryId(this, categoryId, false);

        for (Item tmpItem : allCategoryItems) {
            int iconId = getResources().getIdentifier("kleiderbuegel", "drawable", "com.gruppe1.pem.challengeme");
            mDataset.add(new ListItemIconName(tmpItem.getId(), iconId, tmpItem.getName(), tmpItem.getImageFile()));
        }
    }

    private void saveItem(CollectionItemsFragment fragment) {
        fragment.saveItem();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                try {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    ((CollectionItemsFragment) mItemsCollectionPagerAdapter.getItem(currentItemPosition)).setBitmap(photo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                ((CollectionItemsFragment) mItemsCollectionPagerAdapter.getItem(currentItemPosition)).setImageUri(selectedImage);
            }
        }
    }

}

