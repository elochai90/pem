package com.gruppe1.pem.challengeme;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

/**
 * Created by Simon on 29.06.2015.
 */
public class ImageLoader {

    public static void setPic(ImageView p_imgView , String p_file) {
        // Get the dimensions of the View
        int targetW = Math.max(p_imgView.getWidth(), 1000);
        int targetH = Math.max(p_imgView.getHeight(), 1000);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        //bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(p_file, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        //bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(p_file, bmOptions);
        p_imgView.setImageBitmap(bitmap);
    }
}
