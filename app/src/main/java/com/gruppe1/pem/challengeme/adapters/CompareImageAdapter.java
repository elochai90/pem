package com.gruppe1.pem.challengeme.adapters;

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

import java.util.ArrayList;

/**
 * Created by bianka on 21.06.2015.
 */
public class CompareImageAdapter extends PagerAdapter {
    Context context;
    private ArrayList<Item> categoryItems;
    private PicassoSingleton picassoSingleton;

    public CompareImageAdapter(Context context){
        this.context = context;
        this.picassoSingleton = PicassoSingleton.getInstance();
    }

    public CompareImageAdapter(Context p_context, int p_position) {
        this.context = p_context;
        Category chosenCategory = Category.getAllCategories(context).get(p_position);
        Log.d("Category: ", chosenCategory.getName());

        categoryItems = Item.getItemsByCategoryId(p_context, chosenCategory.getId());

        for(int i = 0; i < categoryItems.size(); i++){
            Log.d("Imagepaths: ", categoryItems.get(i).getImageFile());
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