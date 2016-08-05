package com.gruppe1.pem.challengeme.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.views.CategoriesFragment;
import com.gruppe1.pem.challengeme.views.CompareFragment;
import com.gruppe1.pem.challengeme.views.WishlistFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bianka on 04.11.2015.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
   private final List<Fragment> mFragmentList = new ArrayList<>();
   private final List<String> mFragmentTitleList = new ArrayList<>();
   private Context context;

   public ViewPagerAdapter(Context context, FragmentManager manager) {
      super(manager);
      this.context = context;
   }

   @Override
   public Fragment getItem(int position) {
      switch (position) {
         case 0:
            return CategoriesFragment.newInstance(0, context.getString(R.string.title_activity_categories));
         case 1:
            return CompareFragment.newInstance(1, context.getString(R.string.title_activity_outfits));
         case 2:
            return WishlistFragment.newInstance(2, context.getString(R.string.title_activity_wishlist));
         default:
            return null;
      }
   }

   @Override
   public int getCount() {
      return 3;
   }

//   public void addFrag(Fragment fragment, String title) {
//      mFragmentList.add(fragment);
//      mFragmentTitleList.add(title);
//      notifyDataSetChanged();
//   }

   @Override
   public CharSequence getPageTitle(int position) {
      switch (position) {
         case 0:
            return context.getString(R.string.title_activity_categories);
         case 1:
            return context.getString(R.string.title_activity_outfits);
         case 2:
            return context.getString(R.string.title_activity_wishlist);
         default:
            return null;
      }
   }
}