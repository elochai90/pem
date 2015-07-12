package com.gruppe1.pem.challengeme.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gruppe1.pem.challengeme.Item;
import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.PicassoSingleton;
import com.gruppe1.pem.challengeme.views.NewCompareActivity;

import java.util.ArrayList;

/**
 * Created by bianka on 21.06.2015.
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
     * @param activity the activity of the context
     * @param items list of the items to fill the pager with
     * @param p_builder the parent builder
     */
    public CompareImageAdapter(Activity activity, ArrayList<Item> items, int p_builder) {
        this.activity = (NewCompareActivity) activity;
        this.context = activity.getApplicationContext();
        this.builder = p_builder;

        categoryItems = items;

        for(int i = 0; i < categoryItems.size(); i++){
            String imageFile = categoryItems.get(i).getImageFile();
            if(imageFile == null){
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