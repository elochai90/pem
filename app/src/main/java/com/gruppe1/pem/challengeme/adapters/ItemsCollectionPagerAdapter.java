package com.gruppe1.pem.challengeme.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.ListItemIconName;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.views.CollectionItemsActivity;
import com.gruppe1.pem.challengeme.views.CollectionItemsFragment;

import java.util.ArrayList;

public class ItemsCollectionPagerAdapter extends FragmentStatePagerAdapter {

   private CollectionItemsActivity context;
   private FragmentManager fragmentManager;
   private ArrayList<ListItemIconName> mDataset = new ArrayList<>();
   private ArrayList<CollectionItemsFragment> mFragments = new ArrayList<>();
   private ArrayList<Item> itemCollection = new ArrayList<>();
   private boolean editItem = false;

   public ItemsCollectionPagerAdapter(FragmentManager fm, CollectionItemsActivity context,
         ArrayList<Item> itemCollection, boolean editItem) {
      super(fm);
      this.fragmentManager = fm;
      this.context = context;
      this.itemCollection = itemCollection;
      this.editItem = editItem;
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
      for (int i = 0; i < mDataset.size(); i++) {

         CollectionItemsFragment fragment = new CollectionItemsFragment();
         Bundle args = new Bundle();
         args.putInt(Constants.EXTRA_ITEM_ID, mDataset.get(i)
               .getElementId());
         args.putBoolean(Constants.EXTRA_ITEM_IS_WISHLIST, mDataset.get(i)
               .isWishlistElement());
         args.putInt(Constants.EXTRA_CATEGORY_ID, itemCollection.get(i)
               .getCategoryId());
         args.putBoolean(Constants.EXTRA_ITEM_EDIT, editItem);
         fragment.setArguments(args);
         mFragments.add(fragment);
      }
   }

   /**
    * initializes the dataset of compares
    */
   private void initDataset() {
      mDataset.clear();

      for (Item tmpItem : itemCollection) {
         int iconId = context.getResources()
               .getIdentifier("kleiderbuegel", "drawable", "com.gruppe1.pem.challengeme");
         if (tmpItem.getIsWish() == 1) {
            mDataset.add(new ListItemIconName(context, "wishlist", tmpItem.getId(), iconId,
                  tmpItem.getName(), tmpItem.getImageFile()));
         } else {
            mDataset.add(
                  new ListItemIconName(context, "item", tmpItem.getId(), iconId, tmpItem.getName(),
                        tmpItem.getImageFile()));
         }
      }
   }
}
