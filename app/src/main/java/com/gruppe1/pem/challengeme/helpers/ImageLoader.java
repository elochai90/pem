package com.gruppe1.pem.challengeme.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

/**
 * Created by Simon on 29.06.2015.
 */
public class ImageLoader {

    /**
     * Loads the image from local path as bitmap
     * @param imageFile name of the file
     * @param p_targetW resulting width
     * @param p_targetH resulting height
     * @return Bitmap file
     */
    public static Bitmap getPicFromFile(String imageFile, int p_targetW, int p_targetH) {
        int targetW = p_targetW;
        int targetH = p_targetH;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile, bmOptions);
        Bitmap cropImg = bitmap;
        if(bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (width > height) {
                int crop = (width - height) / 2;
                cropImg = Bitmap.createBitmap(bitmap, crop, 0, height, height);
            } else {
                int crop = (height - width) / 2;
                cropImg = Bitmap.createBitmap(bitmap, 0, crop, width, width);
            }
        }

        return cropImg;
    }
}
