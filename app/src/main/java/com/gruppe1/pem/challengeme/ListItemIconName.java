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
    public String itemFile;

    public ListItemIconName(int elementId, int icon, String name, String itemBitmap)
    {
        this.elementId = elementId;
        this.icon = icon;
        this.name = name;
        this.itemFile = itemBitmap;

//        Log.e("###LIST", this.name);
    }

    public ListItemIconName(int elementId, int icon, String name, String itemFile, Date date)
    {
        this.elementId = elementId;
        this.icon = icon;
        this.name = name;
        this.itemFile = itemFile;
        this.date = date;
    }
}