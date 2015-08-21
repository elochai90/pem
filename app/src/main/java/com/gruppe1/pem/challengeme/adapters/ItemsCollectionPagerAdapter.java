package com.gruppe1.pem.challengeme.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;
import com.gruppe1.pem.challengeme.views.CollectionItemsActivity;
import com.gruppe1.pem.challengeme.views.CollectionItemsFragment;

import java.util.ArrayList;

/**
 * Created by bianka on 20.08.2015.
 */

// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
public class ItemsCollectionPagerAdapter extends FragmentStatePagerAdapter {

    private CollectionItemsActivity context;
    private FragmentManager fragmentManager;
    private ArrayList<ListItemIconName> mDataset = new ArrayList<>();
    private ArrayList<CollectionItemsFragment> mFragments = new ArrayList<>();
    private int categoryId;

    public ItemsCollectionPagerAdapter(FragmentManager fm, CollectionItemsActivity context, int categoryId) {
        super(fm);
        this.fragmentManager = fm;
        this.context = context;
        this.categoryId = categoryId;
        initDataset();
        initFragments();
    }

    @Override
    public Fragment getItem(int i) {
        return mFragments.get(i);
    }

    @Override
    public int getCount() {
        initDataset();
        return mDataset.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }

    private void initFragments() {
        for(int i = 0; i < mDataset.size(); i++) {
            CollectionItemsFragment fragment = new CollectionItemsFragment();
            Bundle args = new Bundle();
            args.putInt(Constants.EXTRA_ITEM_ID, mDataset.get(i).getElementId());
            fragment.setArguments(args);
            mFragments.add(fragment);
        }
    }

    /**
     * initializes the dataset of compares
     */
    private void initDataset() {
        mDataset.clear();

        DataBaseHelper db_helper = new DataBaseHelper(context);
        db_helper.init();

        ArrayList<Item> allCategoryItems = Item.getItemsByCategoryId(context, categoryId, false);

        for (Item tmpItem : allCategoryItems) {
            int iconId = context.getResources().getIdentifier("kleiderbuegel", "drawable", "com.gruppe1.pem.challengeme");
            mDataset.add(new ListItemIconName(tmpItem.getId(), iconId, tmpItem.getName(), tmpItem.getImageFile()));
        }
        db_helper.close();
    }
}
