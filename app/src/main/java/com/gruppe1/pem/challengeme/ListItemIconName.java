package com.gruppe1.pem.challengeme;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Date;

/**
 * Created by bianka on 05.06.2015.
 */
public class ListItemIconName {

    // TODO: not public but  getters and setters
    public int elementId;
    public int icon;
    public String name;
    public Date date;
    public Bitmap itemBitmap;

    public ListItemIconName(int elementId, int icon, String name, Bitmap itemBitmap)
    {
        this.elementId = elementId;
        this.icon = icon;
        this.name = name;
        this.itemBitmap = itemBitmap;

//        Log.e("###LIST", this.name);
    }

    public ListItemIconName(int elementId, int icon, String name, Bitmap itemBitmap, Date date)
    {
        this.elementId = elementId;
        this.icon = icon;
        this.name = name;
        this.itemBitmap = itemBitmap;
        this.date = date;
    }
}