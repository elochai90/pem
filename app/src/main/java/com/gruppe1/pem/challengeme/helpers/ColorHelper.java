package com.gruppe1.pem.challengeme.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.gruppe1.pem.challengeme.Category;

import java.util.ArrayList;

/**
 * Created by bianka.roppelt on 25/07/16.
 */
public class ColorHelper {

   public static double colorDistance(int c1, int c2)
   {
      int red1 = Color.red(c1);
      int red2 = Color.red(c2);
      int rmean = (red1 + red2) >> 1;
      int r = red1 - red2;
      int g = Color.green(c1) - Color.green(c2);
      int b = Color.blue(c1) - Color.blue(c2);
      return Math.sqrt((((512+rmean)*r*r)>>8) + 4*g*g + (((767-rmean)*b*b)>>8));
   }

   public static com.gruppe1.pem.challengeme.Color getClosestColor(int color, ArrayList<com.gruppe1.pem.challengeme.Color> allColors) {
      double minDist = 1000;
      com.gruppe1.pem.challengeme.Color closestColor = null;
      for(int i = 0; i < allColors.size(); i++) {
         double dist = colorDistance(color, Color.parseColor(allColors.get(i).getHexColor()));
         if(dist < minDist) {
            minDist = dist;
            closestColor = allColors.get(i);
         }
      }
      return closestColor;
   }

   public static com.gruppe1.pem.challengeme.Color getClosestColor(String color, ArrayList<com.gruppe1.pem.challengeme.Color> allColors) {
      int colorInt = Integer.parseInt(color, 16) + 0xFF000000;
      return getClosestColor(colorInt, allColors);
   }

   public static int calculateMinDarkColor(String color) {
      int colorHex = Integer.parseInt(color, 16) + 0xFF000000;
      float[] hsv = new float[3];
      android.graphics.Color.colorToHSV(colorHex, hsv);
      if(hsv[2] > 0.75f) {
         hsv[2] -= 0.25f;
      }
      colorHex = android.graphics.Color.HSVToColor(hsv);
      return colorHex;
   }

   public static int calculateMinDarkColor(int colorHex) {
      float[] hsv = new float[3];
      android.graphics.Color.colorToHSV(colorHex, hsv);
      if(hsv[2] > 0.75f) {
         hsv[2] -= 0.25f;
      }
      colorHex = android.graphics.Color.HSVToColor(hsv);
      return colorHex;
   }


   public static Drawable filterIconColor(Context context, String icon, int colorHex) {
      int iconId = context.getResources().getIdentifier(icon, "drawable",
            "com.gruppe1.pem.challengeme");
      Drawable iconDrawable = context.getDrawable(iconId);
//      Drawable copyIconDrawable = null;
      if(iconDrawable != null) {
//         copyIconDrawable = iconDrawable.getConstantState().newDrawable();
      iconDrawable.mutate().setColorFilter( colorHex, PorterDuff.Mode.MULTIPLY );
      }
      return iconDrawable;
   }
}
