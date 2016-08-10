package com.gruppe1.pem.challengeme.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.R;
import com.gruppe1.pem.challengeme.SquareImageViewWidth;
import com.gruppe1.pem.challengeme.helpers.CategoryDataSource;
import com.gruppe1.pem.challengeme.helpers.ColorHelper;
import com.gruppe1.pem.challengeme.helpers.PicassoSingleton;
import com.gruppe1.pem.challengeme.views.NewCompareActivity;

import java.util.ArrayList;

/**
 * PagerAdapter to fill the compare views
 */
public class CompareImageAdapter extends PagerAdapter {
   private Context context;
   private ArrayList<Item> categoryItems;
   private PicassoSingleton picassoSingleton;
   private NewCompareActivity activity;
   private int builder;

   /**
    * Constructor of the CompareImageAdapter
    *
    * @param activity  the activity of the context
    * @param items     list of the items to fill the pager with
    * @param p_builder the parent builder
    */
   public CompareImageAdapter(Activity activity, ArrayList<Item> items, int p_builder) {
      this.activity = (NewCompareActivity) activity;
      this.context = activity.getApplicationContext();
      this.builder = p_builder;

      categoryItems = items;

      for (int i = 0; i < categoryItems.size(); i++) {
         String imageFile = categoryItems.get(i)
               .getImageFile();
         if (imageFile == null) {
            categoryItems.remove(i);
         }
      }

      this.picassoSingleton = PicassoSingleton.getInstance(activity);
   }

   @Override
   public int getCount() {
      return categoryItems.size();
   }

   @Override
   public boolean isViewFromObject(View view, Object object) {
      return view == object;
   }

   @Override
   public Object instantiateItem(ViewGroup container, int position) {

      LayoutInflater inflater = LayoutInflater.from(context);
      ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.item_view_pager, container, false);
      SquareImageViewWidth imageView = (SquareImageViewWidth) layout.findViewById(R.id.imageView);

      Item item = categoryItems.get(position);
      CategoryDataSource categoryDataSource = new CategoryDataSource(context);
      Category category = categoryDataSource.getCategory(item.getCategoryId());

      int colorHex = ColorHelper.calculateMinDarkColor(category.getColor());
      Drawable icon = ColorHelper.filterIconColor(context, category.getIcon(), colorHex);
      picassoSingleton.setImageFit(item.getImageFile(), imageView, icon, icon);

      imageView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            activity.showCategoryChooser(builder);
         }
      });

      container.addView(layout);
      return layout;
   }

   @Override
   public int getItemPosition(Object object) {
      return categoryItems.indexOf(object);
   }

   @Override
   public void destroyItem(ViewGroup container, int position, Object object) {
      container.removeView((View) object);
   }
}