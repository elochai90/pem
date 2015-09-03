package com.gruppe1.pem.challengeme.helpers;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Created by bianka on 03.09.2015.
 */
public class ImageDominantColorExtractor {

    static ImageDominantColorExtractor instance;
    private Bitmap currentBitmap;

    public static ImageDominantColorExtractor getInstance() {
        if(instance == null) {
            instance = new ImageDominantColorExtractor();
        }
        return instance;
    }

    public Integer getDominantColor(Bitmap image) {
        currentBitmap = image;
        Map histogram = new HashMap();

        for(int i = 0; i < image.getWidth(); i++)
            for(int j = 0; j < image.getHeight(); j++) {
                int rgb = image.getPixel(i,j);

                int rgbArray[] = getRGBArrayFromPixel(rgb);

                if(!isGray(rgbArray)) {

                    Integer counter = (Integer) histogram.get(rgb);

                    if (counter == null)
                        counter = 0;
                    counter++;

                    histogram.put(rgb, counter);
                }
            }

        return getMostCommonColor(histogram);
    }

    private int[] getRGBArrayFromPixel(int pixel) {

        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        return new int[]{red,green,blue};

    }

    private boolean isGray(int[] rgbArr) {

        int rgDiff = rgbArr[0] - rgbArr[1];
        int rbDiff = rgbArr[0] - rgbArr[2];

        int tolerance = 100;

        if (rgDiff > tolerance || rgDiff < -tolerance)
            if (rbDiff > tolerance || rbDiff < -tolerance)
                return false;
        return true;
    }

    private int getMostCommonColor(Map histogram) {

        List list = new LinkedList(histogram.entrySet());

        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {

                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());

            }

        });

        if(list.size() <= 0) {
            return getAverageRGB(currentBitmap);
        } else {
            Map.Entry me = (Map.Entry) list.get(list.size() - 1);
            int[] rgb = getRGBArrayFromPixel((Integer) me.getKey());

            String color = "";
            for (int i = 0; i < 3; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toHexString(rgb[i]));
                //            String rgb_value = Integer.toHexString(rgb[i]) + "";

                if (sb.length() < 2) {
                    sb.insert(0, '0'); // pad with leading zero if needed
                }
                color += sb.toString();
            }

            return Color.parseColor("#"+color);
        }
    }


    private int getAverageRGB(Bitmap image) {

        if (null == image) return Color.TRANSPARENT;
        else {

            int pixelCount = image.getWidth() * image.getHeight();
            int red, green, blue;
            red = green = blue = 0;

            for (int i = 0; i < image.getWidth(); i++)
                for (int j = 0; j < image.getHeight(); j++) {
                    int pixel = image.getPixel(i, j);

                    red += Color.red(pixel);
                    green += Color.green(pixel);
                    blue += Color.blue(pixel);
                }

            red /= pixelCount;
            green /= pixelCount;
            blue /= pixelCount;

            return Color.rgb(red, green, blue);
        }
    }
}