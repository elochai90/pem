package com.gruppe1.pem.challengeme.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Singleton of Picasso Library
 */
public class PicassoSingleton {

   private Context context;
   private static PicassoSingleton picassoSingleton;

   private PicassoSingleton(Context p_context) {
      this.context = p_context;
   }

   /**
    * checks on the existance of the Singleton
    *
    * @param p_context Application context
    * @return PicassoSingleton
    */
   public static PicassoSingleton getInstance(Context p_context) {
      if (picassoSingleton == null) {
         picassoSingleton = new PicassoSingleton(p_context);
      }
      return picassoSingleton;
   }

   /**
    * Sets an image to a ImageView
    *
    * @param p_file       filename
    * @param p_targetView target ImageView
    */
   public void setImageFit(String p_file, ImageView p_targetView, Drawable placeHolder,
         Drawable error) {
      if (!p_file.equals("")) {
         Picasso.with(context)
               .load(new File(p_file))
               .placeholder(placeHolder)
               .error(error)
               .fit()
               .centerCrop()
               .into(p_targetView);
      }
   }
}
