package com.gruppe1.pem.challengeme.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.ImageLoader;
import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.helpers.PicassoSingleton;
import com.gruppe1.pem.challengeme.views.NewCompareActivity;

import java.util.ArrayList;

/**
 * Created by bianka on 21.06.2015.
 */
public class CompareImageAdapter extends PagerAdapter {
    Context context;
    private ArrayList<Item> categoryItems;
    private PicassoSingleton picassoSingleton;
    NewCompareActivity activity;
    public int builder;

    public CompareImageAdapter(Activity activity){
        this.activity = (NewCompareActivity)activity;
        this.context = activity.getApplicationContext();
        this.context = activity.getApplicationContext();
        this.picassoSingleton = PicassoSingleton.getInstance();
    }

    public CompareImageAdapter(Activity activity, int p_position, int p_builder) {
        this.activity = (NewCompareActivity) activity;
        this.context = activity.getApplicationContext();
        Category chosenCategory = Category.getAllCategories(context).get(p_position);
        Log.d("Category: ", chosenCategory.getName());
        this.builder = p_builder;

        categoryItems = Item.getItemsByCategoryId(activity, chosenCategory.getId());

        for(int i = 0; i < categoryItems.size(); i++){
            String imageFile = categoryItems.get(i).getImageFile();
            if(imageFile == null){
                categoryItems.remove(i);
            }
        }

        this.picassoSingleton = PicassoSingleton.getInstance();
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
        ImageView imageView = new ImageView(context);
        imageView.setPadding(0, 0, 0, 0);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        String imageFile = categoryItems.get(position).getImageFile();
        this.picassoSingleton.setImage(imageFile, Constants.COMPARE_IMAGE_WIDTH,Constants.COMPARE_IMAGE_HEIGHT, imageView);
        container.addView(imageView, 0);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showCategoryChooser(builder);
            }
        });

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }

}