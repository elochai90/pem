package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by bianka on 21.06.2015.
 */
public class CompareImageAdapter extends PagerAdapter {
    Context context;
    private ArrayList<Item> categoryItems;

    CompareImageAdapter(Context context){
        this.context = context;
    }

    CompareImageAdapter(Context p_context, int p_position) {
        this.context = p_context;
        Category chosenCategory = Category.getAllCategories(context).get(p_position - 1);
        categoryItems = Item.getItemsByCategoryId(p_context, chosenCategory.getId());
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
        ImageLoader.setPic(imageView, imageFile);
        Log.e("###COMP###", imageFile);
        container.addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }

    static class ViewHolder {
        TextView imageTitle;
        TextView rightTextView;
        ImageView image;
    }

}